package com.coagronet.bloque.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.bloque.Bloque;

public interface BloqueRepository extends JpaRepository<Bloque, Integer> {

    @Query(value = "select b.* from bloque as b, sede as s \n" +
            "where b.blo_sede_id = s.sed_id \n" +
            "and b.blo_estado != 2\n" +
            "and s.sed_empresa_id = :empresaId\n" +
            "and b.blo_sede_id = :sedeId\n" +
            "order by blo_nombre asc", nativeQuery = true)
    List<Bloque> buscarBloquePorSede(@Param("sedeId") Long sedeId, @Param("empresaId") Long empresaId);

}
