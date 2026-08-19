package com.inventario.ocupacion.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.ocupacion.Ocupacion;

public interface OcupacionRepository extends JpaRepository<Ocupacion, Long> {

	Optional<Ocupacion> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Ocupacion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
