package com.albo.prSuma.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.albo.prSuma.model.ResetToken;
import com.albo.prSuma.repository.IResetTokenRepository;
import com.albo.prSuma.service.IResetTokenService;


@Service
public class ResetTokenServiceImpl implements IResetTokenService {

	@Autowired
	private IResetTokenRepository dao;

	@Override
	public ResetToken findByToken(String token) {
		return dao.findByToken(token);
	}

	@Override
	public void guardar(ResetToken token) {
		dao.save(token);

	}

	@Override
	public void eliminar(ResetToken token) { 
		dao.delete(token);
	}

}
