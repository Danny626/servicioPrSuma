package com.albo.prSuma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.albo.prSuma.model.AccessTokenSuma;

@Repository
public interface IAccessTokenSumaRepository extends JpaRepository<AccessTokenSuma, Long> {

	@Query("FROM AccessTokenSuma at WHERE at.usuario = :usuario AND at.recinto.recCod = :recCod")
	AccessTokenSuma buscarPorUsuario(
		@Param("usuario") String usuario,
		@Param("recCod") String recCod
	);
	
}
