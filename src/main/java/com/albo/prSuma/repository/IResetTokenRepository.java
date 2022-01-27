package com.albo.prSuma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.albo.prSuma.model.ResetToken;

@Repository
public interface IResetTokenRepository extends JpaRepository<ResetToken, Long>{
	
	//from token where token = :? 
	ResetToken findByToken(String token);

}
