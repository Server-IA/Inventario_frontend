package com.coagronet.modulo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.modulo.Modulo;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {

	@Query("select m.nombreId, m.nombre, m.url, m.icon from Modulo m where m.estado.id = :estadoId and m.tipoAplicacion.id = :tipoAplicacionId order by m.id desc")
	Modulo findByEstadoIdAndTipoAplicacionId(@Param("estadoId") Long estadoId,
			@Param("tipoAplicacionId") Long tipoAplicacionId);

}
