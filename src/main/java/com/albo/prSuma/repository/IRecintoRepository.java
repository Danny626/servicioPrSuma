package com.albo.prSuma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.albo.prSuma.model.Recinto;


@Repository
public interface IRecintoRepository extends JpaRepository<Recinto, String> {

}
