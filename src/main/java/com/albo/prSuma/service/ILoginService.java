package com.albo.prSuma.service;

import com.albo.prSuma.model.Usuario;

public interface ILoginService {

	Usuario verificarNombreUsuario(String usuario, String estado);

	int cambiarClave(String clave, String nombre) throws Exception;

}
