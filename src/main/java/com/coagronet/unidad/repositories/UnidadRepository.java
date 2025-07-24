package com.coagronet.unidad.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.unidad.Unidad;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {

	Optional<Unidad> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Unidad> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
