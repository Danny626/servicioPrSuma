package com.albo.prSuma.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.albo.prSuma.model.Usuario;


@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, String> {

	Usuario findOneByUsername(String username);

	@Query("FROM Usuario us where us.username = :username")
	Usuario listarPorUsername(@Param("username") String username);

	@Query("FROM Usuario us WHERE us.username LIKE :username")
	Page<Usuario> buscarXNombre(Pageable pageable, @Param("username") String username);
}