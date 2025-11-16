package com.coagronet.cierreinventario.repositories;

import com.coagronet.cierreinventario.CierreInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CierreInventarioRepository extends JpaRepository<CierreInventario, Long> {

    List<CierreInventario> findByEmpresa_Id(Long empresaId);

    @Query("""
       SELECT COUNT(ci) > 0\s
       FROM CierreInventario ci
       WHERE ci.empresa.id = :empresaId
         AND ci.almacen.id = :almacenId
         AND ci.fechaInicio = :fechaInicio
         AND ci.fechaCorte = :fechaCorte
      \s""")
    boolean existeCierreEnMes(
            @Param("empresaId") Long empresaId,
            @Param("almacenId") Long almacenId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaCorte") LocalDate fechaCorte
    );
}
