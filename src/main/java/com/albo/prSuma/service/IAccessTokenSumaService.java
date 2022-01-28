package com.albo.prSuma.service;

import com.albo.prSuma.model.AccessTokenSuma;

public interface IAccessTokenSumaService extends IService<AccessTokenSuma, Long> {

	AccessTokenSuma buscarPorUsuario(String usuario, String recCod);
}
