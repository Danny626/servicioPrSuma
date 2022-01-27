package com.albo.prSuma.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.albo.prSuma.model.Aduana;
import com.albo.prSuma.repository.IAduanaRepository;
import com.albo.prSuma.service.IAduanaService;

@Service
public class AduanaServiceImpl implements IAduanaService {

	@Autowired
	private IAduanaRepository aduanaRepo;

	@Override
	public List<Aduana> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Aduana> findById(Integer id) {
		return aduanaRepo.findById(id);
	}

	@Override
	public Aduana saveOrUpdate(Aduana t) {
		return aduanaRepo.save(t);
	}

	@Override
	public Boolean deleteById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

}
