package com.albo.prSuma.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.albo.prSuma.model.Rol;

public interface IRolService extends IService<Rol, String> {

	Page<Rol> listarPageable(Pageable pageable);
}
