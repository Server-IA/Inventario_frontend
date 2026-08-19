package com.inventario.espacioOcupacion.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.espacioOcupacion.EspacioOcupacion;

public interface EspacioOcupacionRepository extends JpaRepository<EspacioOcupacion, Long> {

	Page<EspacioOcupacion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<EspacioOcupacion> findByIdAndEmpresaId(Long id, Long empresaId);

}
