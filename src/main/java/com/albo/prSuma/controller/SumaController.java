package com.albo.prSuma.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.albo.prSuma.dto.BodyRegistroPartesSuma;
import com.albo.prSuma.dto.BodyVerPreSuma;
import com.albo.prSuma.dto.ParamsLoginSuma;
import com.albo.prSuma.dto.RespVerificaTokenSuma;
import com.albo.prSuma.dto.ResultLoginSuma;
import com.albo.prSuma.dto.ResultTokenSuma;
import com.albo.prSuma.model.AccessTokenSuma;
import com.albo.prSuma.model.Recinto;
import com.albo.prSuma.service.IAccessTokenSumaService;
import com.albo.prSuma.service.IRecintoService;
import com.albo.suma.model.ParteSumaProceso;
import com.albo.suma.model.pre.PreSuma;

@RestController
@RequestMapping("/suma")
public class SumaController {
	
	private static final Logger LOGGER = LogManager.getLogger(SumaController.class);
	private final String URI_LOGIN_SUMA = "/b-sso/rest/autenticar/portal?operador=ip";
	private final String URI_VERIFICA_TOKEN_SUMA = "/b-sso/rest/autenticar/verificar";
	private final String URI_MIS_PARTES_SUMA = "/b-ingreso/api/json/pre/120585022/prms";
	private final String URI_CONTEO_PARTES_SUMA = "/b-ingreso/api/json/pre/120585022/count";
	private final String URI_VER_PRE_SUMA = "/b-ingreso/api/json/pre/";
	
	@Value("${suma.api.host.baseurl}")
	private String apiHost;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private IAccessTokenSumaService accessTokenSumaService;
	
	@Autowired
	private IRecintoService recintoService;
	
	
	/**
	 * Realiza el login en el sistema de suma
	 * @param paramsLoginSuma
	 * @return Un objeto de tipo ResultLoginSuma si todo es correcto
	 */
	@PostMapping(value = "/loginSuma", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> loginSuma(@RequestBody ParamsLoginSuma paramsLoginSuma) {

		ResultLoginSuma resultLoginError = new ResultLoginSuma();
		resultLoginError.setSuccess(false);
		resultLoginError.setResult(null);
		
		Optional<Recinto> recinto = this.recintoService.findById(paramsLoginSuma.getCodRecinto());
		
		if( recinto.isEmpty() ) {
			LOGGER.error("Error. Cód. de recinto no válido");
			return new ResponseEntity<Object>("Error. Cód. de recinto no válido", HttpStatus.BAD_REQUEST);
		}
		
		String codAduanaUsuarioAlbo = recinto.get().getAduana().getAduCod().toString();
		
		// buscamos en bd AccessTokenSuma si existe un token registrado para el usuario
		// y si el token es válido aún
		AccessTokenSuma accessTokenSuma = this.accessTokenSumaService.buscarPorUsuario(paramsLoginSuma.getBodyLoginSuma().getNombreUsuario(), recinto.get().getRecCod());
		
		if(accessTokenSuma != null) {
			
			// verificamos la validez del token
			ResponseEntity<RespVerificaTokenSuma> revisaToken = this.verificaTokenSuma(accessTokenSuma.getToken());
			
			if (revisaToken.getBody().isSuccess() == true) {
				
				String codAduanaUsuarioSuma = revisaToken.getBody().getResult().getUsuario().getAduana().getCodigo();
				
				// verificamos si el codRecinto corresponde con el recinto al que pertenece el usuario suma
				if(!codAduanaUsuarioSuma.equals(codAduanaUsuarioAlbo)) {
					LOGGER.error("Error. Cód. Aduana usuario SUMA difiere de cód. Aduana usuario ALBO");
					return new ResponseEntity<>("Error. Cód. Aduana usuario SUMA difiere de cód. Aduana usuario ALBO", HttpStatus.BAD_REQUEST);
				}
					
				ResultTokenSuma resultTokenSuma = new ResultTokenSuma();
				resultTokenSuma.setToken(accessTokenSuma.getToken());
				resultTokenSuma.setUrl("/portal/listener.html#/listener");
				
				ResultLoginSuma resultLoginSuma = new ResultLoginSuma();
				resultLoginSuma.setSuccess(true);
				resultLoginSuma.setResult(resultTokenSuma);
				
				return new ResponseEntity<>(resultLoginSuma, HttpStatus.OK);
			}
			
			this.eliminaTokenUsuarioSuma(paramsLoginSuma.getBodyLoginSuma().getNombreUsuario(), paramsLoginSuma.getCodRecinto());
			
			ResponseEntity<Object> respProcesoLoginSuma = this.procesoRequestLoginSuma(paramsLoginSuma, recinto.get());
			
			if(!respProcesoLoginSuma.getStatusCode().equals(HttpStatus.OK)) {
				LOGGER.error("Error. Ocurrió un error en el proceso de Login con SUMA");
				return new ResponseEntity<>("Error. Ocurrió un error en el proceso de Login con SUMA", respProcesoLoginSuma.getStatusCode());
			}
			
			// verificamos la validez del token
			ResultLoginSuma resProcesoLoginSuma = (ResultLoginSuma) respProcesoLoginSuma.getBody();
			ResponseEntity<RespVerificaTokenSuma> revisaToken_2 = this.verificaTokenSuma(resProcesoLoginSuma.getResult().getToken());
			String codAduanaUsuarioSuma_2 = revisaToken_2.getBody().getResult().getUsuario().getAduana().getCodigo();
			
			// verificamos si el codRecinto corresponde con el recinto al que pertenece el usuario suma
			if(!codAduanaUsuarioSuma_2.equals(codAduanaUsuarioAlbo)) {
				LOGGER.error("Error. Cód. Aduana usuario SUMA difiere de cód. Aduana usuario ALBO");
				return new ResponseEntity<>("Error. Cód. Aduana usuario SUMA difiere de cód. Aduana usuario ALBO", HttpStatus.BAD_REQUEST);
			}		
			
			return respProcesoLoginSuma;
		} else {
			ResponseEntity<Object> respProcesoLoginSuma = this.procesoRequestLoginSuma(paramsLoginSuma, recinto.get());
			
			if(!respProcesoLoginSuma.getStatusCode().equals(HttpStatus.OK)) {
				LOGGER.error("Error. Ocurrió un error en el proceso de Login con SUMA");
				return new ResponseEntity<>("Error. Ocurrió un error en el proceso de Login con SUMA", respProcesoLoginSuma.getStatusCode());
			}
			
			// verificamos la validez del token
			ResultLoginSuma resProcesoLoginSuma = (ResultLoginSuma) respProcesoLoginSuma.getBody();
			ResponseEntity<RespVerificaTokenSuma> revisaToken_2 = this.verificaTokenSuma(resProcesoLoginSuma.getResult().getToken());
			String codAduanaUsuarioSuma = revisaToken_2.getBody().getResult().getUsuario().getAduana().getCodigo();
			
			// verificamos si el codRecinto corresponde con el recinto al que pertenece el usuario suma
			if(!codAduanaUsuarioSuma.equals(codAduanaUsuarioAlbo)) {
				LOGGER.error("Error. Cód. Aduana usuario SUMA difiere de cód. Aduana usuario ALBO");
				return new ResponseEntity<>("Error. Cód. Aduana usuario SUMA difiere de cód. Aduana usuario ALBO", HttpStatus.BAD_REQUEST);
			}		
			
			return respProcesoLoginSuma;
		}
	}
	
	@GetMapping(value = "/verificaTokenSuma/{tk}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespVerificaTokenSuma> verificaTokenSuma(@PathVariable("tk") String tk) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
		headers.set("Accept", "application/json, text/plain, */*");
		headers.set("sec-ch-ua-mobile", "?0");
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
		headers.set("sec-ch-ua-platform", "\"Windows\"");
		headers.set("Content-Type", "application/json;charset=UTF-8");
//		headers.set("Connection", "keep-alive");
		
		String urlAutenticacion = URI_VERIFICA_TOKEN_SUMA + "/" + tk;

		RequestEntity<?> request = RequestEntity.get(urlAutenticacion).headers(headers).build();

		ResponseEntity<RespVerificaTokenSuma> response = restTemplate.exchange(request, RespVerificaTokenSuma.class);
		
		if(response.getBody().isSuccess() == true) {
			return new ResponseEntity<RespVerificaTokenSuma>(response.getBody(), response.getStatusCode());
		}
		
		return new ResponseEntity<RespVerificaTokenSuma>(new RespVerificaTokenSuma(), response.getStatusCode());
	}
	
	@GetMapping(value = "/conteoPartesSuma", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> conteoPartesSuma(String userName, String token, List<String> listaTipPre, String search, LocalDateTime desde, LocalDateTime hasta, List<String> listaEstadosPartes) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
		headers.set("sec-ch-ua-mobile", "?0");
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
		headers.set("Content-Type", "application/json;charset=UTF-8");
		headers.set("Accept", "application/json, text/plain, */*");
		headers.set("User", userName);
		headers.set("Auth-Token", token);
		headers.set("sec-ch-ua-platform", "\"Windows\"");
		
		// armado url
		String urlConteoPrm = URI_CONTEO_PARTES_SUMA + "?";

		for (String tipPre : listaTipPre) {
			urlConteoPrm = urlConteoPrm + "&tipPre=" + tipPre;
		}

		urlConteoPrm = urlConteoPrm 
				+ "&search=" + search
				+ "&to=" + this.localDateTimeToEpochMilliseconds(hasta)
				+ "&from="
				+ this.localDateTimeToEpochMilliseconds(desde)
				+ "&column=" + "cor";

		RequestEntity<?> request = RequestEntity.post(urlConteoPrm).headers(headers)
				.body(listaEstadosPartes);
	
		ResponseEntity<Integer> response = new ResponseEntity<>(null, HttpStatus.OK);
		
		try {
			response = this.restTemplate.exchange(request, Integer.class);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error obteniendo conteo partes suma: " + e.getResponseBodyAsString());
			return new ResponseEntity<String>("Error obteniendo conteo partes suma", response.getStatusCode());
		}
		
		LOGGER.info("El conteo de partes es: " + response.getBody());

		return response;
	}
	
	/**
	 * Busca un parte del sistema suma
	 * @param bodyRegistroPartesSuma
	 * @return Un objeto del tipo ResultadoRegistroPartesSuma si todo sale correcto
	 */
	@PostMapping(value = "/busquedaParteSuma", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> busquedaParteSuma(@RequestBody BodyRegistroPartesSuma bodyRegistroPartesSuma) {
		
		// armamos la fecha proceso
		LocalDateTime fechaProceso = LocalDateTime.now();
		LOGGER.info("fechaProceso: " + fechaProceso);
		
		bodyRegistroPartesSuma.getParamsMisPartesSuma().setFrom(fechaProceso);
		bodyRegistroPartesSuma.getParamsMisPartesSuma().setTo(fechaProceso);
		
		// hacemos el request de registros suma, recorriendo sus páginas de partes según la fecha		
		// verificamos la validez del token
		ParamsLoginSuma paramsLoginSuma = new ParamsLoginSuma();
		paramsLoginSuma.setCodRecinto(bodyRegistroPartesSuma.getCodRecinto());
		paramsLoginSuma.setBodyLoginSuma(bodyRegistroPartesSuma.getBodyLoginSuma());
		
		ResponseEntity<Object> responseLoginSuma = this.loginSuma(paramsLoginSuma);
		ResultLoginSuma resultLoginSuma = new ResultLoginSuma();
		
		switch (responseLoginSuma.getStatusCode()) {
		case OK:
			resultLoginSuma = (ResultLoginSuma) responseLoginSuma.getBody();
			bodyRegistroPartesSuma.setToken(resultLoginSuma.getResult().getToken());
			
			// realizamos la consulta de la cantidad de partes existentes en suma 
			ResponseEntity<?> conteoPrSuma = this.conteoPartesSuma(bodyRegistroPartesSuma.getUsuario(), 
					bodyRegistroPartesSuma.getToken(), bodyRegistroPartesSuma.getParamsMisPartesSuma().getTipPre(), 
					bodyRegistroPartesSuma.getParamsMisPartesSuma().getSearch(), fechaProceso, fechaProceso, 
					bodyRegistroPartesSuma.getBodyMisPartesSuma().getEstadoParte());
			
			if(conteoPrSuma.getBody().toString().equals("0")) {
				LOGGER.info("No existen PR en SUMA" + conteoPrSuma.getStatusCode() + ' ' + conteoPrSuma.getBody());
				return new ResponseEntity<>("No existen PR en SUMA", conteoPrSuma.getStatusCode());
			} else {
				// realizamos la búsqueda de parte suma
				ResponseEntity<List<ParteSumaProceso>> listaPartesSumaResultado = this.requestBusquedaParteSuma(bodyRegistroPartesSuma);

				// si la consulta del conteo de partes suma es correcto
				if(conteoPrSuma.getStatusCode() == HttpStatus.OK) {
					if( Integer.valueOf(conteoPrSuma.getBody().toString()) > 1 ) {
						LOGGER.error("Error: " + "Existen " + conteoPrSuma.getBody().toString() + " resultados con esa búsqueda");
						return new ResponseEntity<>("Existen " + conteoPrSuma.getBody().toString() + " resultados con esa búsqueda", HttpStatus.CONFLICT);
					}
				}
				
				return new ResponseEntity<>(listaPartesSumaResultado.getBody().get(0), HttpStatus.OK);
			}

		default:
			LOGGER.error("Error login SUMA: " + responseLoginSuma.getStatusCode() + ' ' + responseLoginSuma.getBody());
			return new ResponseEntity<>(responseLoginSuma.getBody(), responseLoginSuma.getStatusCode());
		}
	}
	
	@PostMapping(value = "/verPreSuma", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> verPreSuma(@RequestBody BodyVerPreSuma bodyVerPreSuma) {
		
		ResponseEntity<Object> responseLoginSuma = this.loginSuma(bodyVerPreSuma.getParamsLoginSuma());
		ResultLoginSuma resultLoginSuma = new ResultLoginSuma();
		
		switch (responseLoginSuma.getStatusCode()) {
		case OK:
			resultLoginSuma = (ResultLoginSuma) responseLoginSuma.getBody();
			bodyVerPreSuma.setTokenSuma(resultLoginSuma.getResult().getToken());
			
			// realizamos el request para ver el prm suma
			ResponseEntity<?> responseVerPreSuma = this.requestVerPreSuma(bodyVerPreSuma);
			
			if(responseVerPreSuma.getStatusCode() != HttpStatus.OK) {
				LOGGER.error("Error: " + responseVerPreSuma.getBody());
				return new ResponseEntity<>(responseVerPreSuma.getBody(), responseVerPreSuma.getStatusCode());
			}
			
			return new ResponseEntity<>(responseVerPreSuma.getBody(), HttpStatus.OK);
			
		default:
			LOGGER.error("Error login SUMA: " + responseLoginSuma.getStatusCode() + ' ' + responseLoginSuma.getBody());
			return new ResponseEntity<>(responseLoginSuma.getBody(), responseLoginSuma.getStatusCode());
		}
		
	}
	
	// Realiza la búsqueda de PR en SUMA
	public ResponseEntity<List<ParteSumaProceso>> requestBusquedaParteSuma(
			BodyRegistroPartesSuma bodyRegistroPartesSuma) {
					
		ResponseEntity<List<ParteSumaProceso>> response = this.requestMisPartesSuma(bodyRegistroPartesSuma);
		
		if(response.getStatusCode() != HttpStatus.OK) {
			LOGGER.error("/registroPartesSuma => " + response.getStatusCode());
			return new ResponseEntity<List<ParteSumaProceso>>(new ArrayList<>(), response.getStatusCode());
		}
		
		return new ResponseEntity<List<ParteSumaProceso>>(response.getBody(), HttpStatus.OK);	
	}
	
	// Realiza el request para ver el pre en SUMA
	public ResponseEntity<?> requestVerPreSuma(BodyVerPreSuma bodyVerPreSuma) {
					
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json, text/plain, */*");
		headers.set("Auth-Token", bodyVerPreSuma.getTokenSuma());
		headers.set("Content-Type", "application/json;charset=UTF-8");
		headers.set("Host", this.apiHost.replace("https://", ""));
		headers.set("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
		headers.set("sec-ch-ua-mobile", "?0");
		headers.set("sec-ch-ua-platform", "\"Windows\"");
		headers.set("User", bodyVerPreSuma.getUserSuma());
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
		
		// armado url
		String urlPre = URI_VER_PRE_SUMA + bodyVerPreSuma.getIdPrmSuma();
		
		RequestEntity<?> request = RequestEntity.get(urlPre).headers(headers).build();
	
		ResponseEntity<?> response = new ResponseEntity<>(new PreSuma(), HttpStatus.OK);
		
		try {
			response = this.restTemplate.exchange(request, PreSuma.class);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error obteniendo el pre suma: " + e.getResponseBodyAsString());
			LOGGER.error("Header error: " + response.getHeaders());
			return new ResponseEntity<>(new PreSuma(), response.getStatusCode());
		}

		return response;
	}
	
	private ResponseEntity<List<ParteSumaProceso>> requestMisPartesSuma(BodyRegistroPartesSuma bodyRegistroPartesSuma) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
		headers.set("sec-ch-ua-mobile", "?0");
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
		headers.set("Content-Type", "application/json;charset=UTF-8");
		headers.set("Accept", "application/json, text/plain, */*");
		headers.set("User", bodyRegistroPartesSuma.getBodyLoginSuma().getNombreUsuario());
		headers.set("Auth-Token", bodyRegistroPartesSuma.getToken());
		headers.set("sec-ch-ua-platform", "\"Windows\"");

		// armado url
		String urlPrm = URI_MIS_PARTES_SUMA + "?" + "page=" + bodyRegistroPartesSuma.getParamsMisPartesSuma().getPage()
				+ "&size=" + bodyRegistroPartesSuma.getParamsMisPartesSuma().getSize() + "&search="
				+ bodyRegistroPartesSuma.getParamsMisPartesSuma().getSearch();

		for (String tipPre : bodyRegistroPartesSuma.getParamsMisPartesSuma().getTipPre()) {
			urlPrm = urlPrm + "&tipPre=" + tipPre;
		}

		urlPrm = urlPrm + "&to="
				+ this.localDateTimeToEpochMilliseconds(bodyRegistroPartesSuma.getParamsMisPartesSuma().getTo())
				+ "&from="
				+ this.localDateTimeToEpochMilliseconds(bodyRegistroPartesSuma.getParamsMisPartesSuma().getFrom())
				+ "&column=" + bodyRegistroPartesSuma.getParamsMisPartesSuma().getColumn();

		RequestEntity<?> request = RequestEntity.post(urlPrm).headers(headers)
				.body(bodyRegistroPartesSuma.getBodyMisPartesSuma().getEstadoParte());
	
		ResponseEntity<List<ParteSumaProceso>> response = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		
		try {
			response = restTemplate.exchange(request,
					new ParameterizedTypeReference<List<ParteSumaProceso>>() {
					});
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error obteniendo partes suma: " + e.getResponseBodyAsString());
			LOGGER.error("Header error: " + response.getHeaders());
			return new ResponseEntity<List<ParteSumaProceso>>(new ArrayList<>(), response.getStatusCode());
		}

		return response;
	}
	
	public Boolean eliminaTokenUsuarioSuma(String usuario, String codRecinto) {
		Boolean flagEliminado = false;
		
		AccessTokenSuma accessTokenSuma = this.accessTokenSumaService.buscarPorUsuario(usuario, codRecinto);
		
		if(accessTokenSuma == null) return false;
		
		flagEliminado = this.accessTokenSumaService.deleteById(accessTokenSuma.getId());
		
		return flagEliminado;
	}
	
	private ResponseEntity<Object> procesoRequestLoginSuma(ParamsLoginSuma paramsLoginSuma, Recinto recinto) {
		
		ResponseEntity<ResultLoginSuma> response = this.requestLoginSuma(paramsLoginSuma);

		switch (response.getStatusCode()) {
		case OK:
			AccessTokenSuma accessTokenSumaGuardar = new AccessTokenSuma();
			accessTokenSumaGuardar.setUsuario(paramsLoginSuma.getBodyLoginSuma().getNombreUsuario());
			accessTokenSumaGuardar.setToken(response.getBody().getResult().getToken());
			accessTokenSumaGuardar.setRecinto(recinto);
			
			// guardamos el token suma
			this.guardadoTokenSuma(accessTokenSumaGuardar);
			
			return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
		default:
			LOGGER.error("Error login SUMA: " + response.getStatusCode() + ' ' + response.getBody());
			return new ResponseEntity<>(response.getBody(), response.getStatusCode());
		}
	}
	
	// función que guarda el token suma según el recinto
	private AccessTokenSuma guardadoTokenSuma(AccessTokenSuma accessTokenSumaGuardar) {
		return this.accessTokenSumaService.saveOrUpdate(accessTokenSumaGuardar);
	}
	
	public ResponseEntity<ResultLoginSuma> requestLoginSuma(ParamsLoginSuma paramsLoginSuma) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
		headers.set("Accept", "application/json, text/plain, */*");
		headers.set("Content-Type", "application/json;charset=UTF-8");
		headers.set("User", paramsLoginSuma.getBodyLoginSuma().getNombreUsuario());
		headers.set("sec-ch-ua-mobile", "?0");
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
		headers.set("sec-ch-ua-platform", "\"Windows\"");
		headers.set("Connection", "keep-alive");

		RequestEntity<?> request = RequestEntity.post(URI_LOGIN_SUMA).headers(headers).body(paramsLoginSuma.getBodyLoginSuma());

		ResponseEntity<ResultLoginSuma> response = restTemplate.exchange(request, ResultLoginSuma.class);
		
		return response;
	}
	
	// convierte una fecha LocalDateTime a un formato epoch
	public Long localDateTimeToEpochMilliseconds(LocalDateTime fechaTime) {
		Instant instant = fechaTime.atZone(ZoneId.systemDefault()).toInstant();
		return instant.toEpochMilli();
	}

}
