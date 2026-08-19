package com.inventario.seccion.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.seccion.Seccion;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {

	Optional<Seccion> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Seccion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
