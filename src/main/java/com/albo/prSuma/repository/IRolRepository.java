package com.albo.prSuma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.albo.prSuma.model.Rol;


@Repository
public interface IRolRepository extends JpaRepository<Rol, String> {

}
