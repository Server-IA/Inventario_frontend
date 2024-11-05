package com.coagronet.almacen.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.almacen.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {

    @Query(value = "select a.* from almacen as a, sede as s \n" +
            "where a.alm_sede_id = s.sed_id \n" +
            "and a.alm_estado != 2\n" +
            "and s.sed_empresa_id = :empresaId\n" +
            "and a.alm_sede_id = :sedeId\n" +
            "order by alm_nombre asc", nativeQuery = true)
    List<Almacen> buscarAlmacenesPorSede(@Param("sedeId") Long sedeId, @Param("empresaId") Long empresaId);

    @Query(value = "select a.* from almacen as a, sede as s \n" +
            "where a.alm_sede_id = s.sed_id \n" +
            "and a.alm_estado != 2\n" +
            "and s.sed_empresa_id = :empresaId\n" +
            "and a.alm_sede_id = :sedeId\n" +
            "order by alm_nombre asc", nativeQuery = true)
    Page<Almacen> buscarAlmacenesPorSedePage(@Param("sedeId") Long sedeId, @Param("empresaId") Long empresaId,
            Pageable paginacion);

}
