package com.coagronet.estado.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.estado.Estado;

public interface EstadoRepository extends JpaRepository<Estado, Long> {

	Optional<Estado> findByIdAndEstadoCategoriaId(Long id, Long estadoCategoriaId);

}
