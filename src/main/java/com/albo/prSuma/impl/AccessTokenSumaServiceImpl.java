package com.albo.prSuma.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.albo.prSuma.model.AccessTokenSuma;
import com.albo.prSuma.repository.IAccessTokenSumaRepository;
import com.albo.prSuma.service.IAccessTokenSumaService;

@Service
public class AccessTokenSumaServiceImpl implements IAccessTokenSumaService {

	@Autowired
	private IAccessTokenSumaRepository accessTokenSumaRepository;

	@Override
	public List<AccessTokenSuma> findAll() {
		return accessTokenSumaRepository.findAll();
	}

	@Override
	public Optional<AccessTokenSuma> findById(Long id) {
		return accessTokenSumaRepository.findById(id);
	}

	@Override
	public AccessTokenSuma saveOrUpdate(AccessTokenSuma t) {
		return accessTokenSumaRepository.save(t);
	}

	@Override
	public Boolean deleteById(Long id) {
		if (!accessTokenSumaRepository.existsById(id)) {
			return false;
		}

		accessTokenSumaRepository.deleteById(id);
		return true;
	}

	@Override
	public AccessTokenSuma buscarPorUsuario(String usuario, String recCod) {
		return accessTokenSumaRepository.buscarPorUsuario(usuario, recCod);
	}

}
