package com.coagronet.produccion.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.produccion.Produccion;

public interface ProduccionRepository extends JpaRepository<Produccion, Integer> {

    @Query(value = "select p.* from produccion as p, espacio as e, bloque as b, sede as s\n" +
            "where p.pro_espacio_id = e.esp_id \n" +
            "and e.esp_bloque_id = b.blo_id \n" +
            "and b.blo_sede_id = s.sed_id\n" +
            "and p.pro_estado != 2\n" +
            "and s.sed_empresa_id = :empresaId\n" +
            "and p.pro_espacio_id = :espacioId\n" +
            "order by pro_nombre asc", nativeQuery = true)
    List<Produccion> buscarProduccionPorEspacioShort(@Param("espacioId") Integer espacioId,
            @Param("empresaId") Long empresaId);

    @Query(value = "select p.* from produccion as p, espacio as e, bloque as b, sede as s\n" +
            "where p.pro_espacio_id = e.esp_id \n" +
            "and e.esp_bloque_id = b.blo_id \n" +
            "and b.blo_sede_id = s.sed_id\n" +
            "and p.pro_estado != 2\n" +
            "and s.sed_empresa_id = :empresaId\n" +
            "and p.pro_espacio_id = :espacioId\n" +
            "order by pro_nombre asc", nativeQuery = true)
    Page<Produccion> buscarProduccionPorEspacioLong(@Param("espacioId") Integer espacioId,
            @Param("empresaId") Long empresaId, Pageable paginacion);

}
