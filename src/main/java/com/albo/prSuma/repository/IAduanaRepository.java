package com.albo.prSuma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.albo.prSuma.model.Aduana;

@Repository
public interface IAduanaRepository extends JpaRepository<Aduana, Integer> {

}
