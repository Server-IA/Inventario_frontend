package com.coagronet.modulo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.modulo.Modulo;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    @Query("select m.nombreId, m.nombre, m.url, m.icon from Modulo m where m.estado.id = :estadoId and m.tipoAplicacion.id = :tipoAplicacionId order by m.id desc")
    Modulo findByEstadoIdAndTipoAplicacionId(@Param("estadoId") Long estadoId,
            @Param("tipoAplicacionId") Long tipoAplicacionId);

    // Buscar varios módulos por su ID de texto (WHERE mod_nombre_id IN (...))
    List<Modulo> findByNombreIdIn(List<String> nombreIds);

    Boolean existsByNombre(String nombre);

}
