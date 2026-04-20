package com.coagronet.produccion.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.produccion.Produccion;

public interface ProduccionRepository extends JpaRepository<Produccion, Long> {

	Optional<Produccion> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Produccion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	@Query("SELECT pr FROM Produccion pr WHERE pr.id = :id AND pr.estado.id = :estadoId")
	Optional<Produccion> findByIdAndEstadoId(@Param("id") Long id, @Param("estadoId") Long estadoId);

}
