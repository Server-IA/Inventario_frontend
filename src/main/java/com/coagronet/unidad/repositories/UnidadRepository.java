package com.coagronet.unidad.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.unidad.Unidad;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {

	Optional<Unidad> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Unidad> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
