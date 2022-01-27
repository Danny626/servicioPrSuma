package com.albo.prSuma.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.albo.prSuma.model.Recinto;
import com.albo.prSuma.repository.IRecintoRepository;
import com.albo.prSuma.service.IRecintoService;

@Service
public class RecintoServiceImpl implements IRecintoService {

	@Autowired
	private IRecintoRepository recintoDao;
	
	@Override
	public List<Recinto> findAll() {
		return recintoDao.findAll();
	}

	@Override
	public Optional<Recinto> findById(String id) {
		return recintoDao.findById(id);
	}

	@Override
	public Recinto saveOrUpdate(Recinto t) {
		return recintoDao.save(t);
	}

	@Override
	public Boolean deleteById(String id) {
		if (!recintoDao.existsById(id)) {
			return false;
		}
		
		recintoDao.deleteById(id);
		return true;
	}

}
