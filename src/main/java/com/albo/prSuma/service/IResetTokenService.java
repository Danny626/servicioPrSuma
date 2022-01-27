package com.albo.prSuma.service;

import com.albo.prSuma.model.ResetToken;

public interface IResetTokenService {

	ResetToken findByToken(String token);

	void guardar(ResetToken token);

	void eliminar(ResetToken token);

}
