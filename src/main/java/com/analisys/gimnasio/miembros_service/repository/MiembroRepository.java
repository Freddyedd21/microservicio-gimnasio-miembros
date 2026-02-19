package com.analisys.gimnasio.miembros_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analisys.gimnasio.miembros_service.model.Miembro;

public interface MiembroRepository extends JpaRepository<Miembro, Long> {

	boolean existsByEmail(String email);
}
