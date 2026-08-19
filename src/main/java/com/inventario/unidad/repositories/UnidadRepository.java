package com.inventario.unidad.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.unidad.Unidad;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {

	Page<Unidad> findAllByOrderById(Pageable pageable);

	Optional<Unidad> findByIdAndEstadoId(Long id, Long estadoId);

}
