package com.coagronet.tipoProduccion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoProduccion.TipoProduccion;

public interface TipoProduccionRepository extends JpaRepository<TipoProduccion, Long> {

	Optional<TipoProduccion> findByIdAndEmpresaId(Long id, Long empresaId);

	List<TipoProduccion> findByEmpresaIdOrderByIdAsc(Long empresaId);

}