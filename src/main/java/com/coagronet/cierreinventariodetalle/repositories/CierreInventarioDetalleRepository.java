package com.coagronet.cierreinventariodetalle.repositories;

import com.coagronet.cierreinventariodetalle.CierreInventarioDetalle;
import com.coagronet.presentacionProducto.PresentacionProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CierreInventarioDetalleRepository extends JpaRepository<CierreInventarioDetalle, Long> {


    Page<CierreInventarioDetalle>findByEmpresaId(Long empresaId, Pageable pageable);

    Optional<CierreInventarioDetalle>findByIdAndEmpresaId(Long id, Long empresaId);


    @Query("""
    SELECT DISTINCT ki.presentacionProducto
    FROM ArticuloKardex ki
    JOIN ki.kardex k
    WHERE k.almacen.id = :almacenId
      AND k.fechaHora BETWEEN :fechaInicio AND :fechaCorte
""")
    List<PresentacionProducto> findPresentacionesEnRango(
            @Param("almacenId") Long almacenId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaCorte") LocalDateTime fechaCorte);



    @Query(value = "SELECT fn_calcular_stock_cierre(:presentacionId, :almacenId, :empresaId, :fechaCorte)",
            nativeQuery = true)
    BigDecimal calcularStock(
            @Param("presentacionId") Long presentacionId,
            @Param("almacenId") Long almacenId,
            @Param("empresaId") Long empresaId,
            @Param("fechaCorte") LocalDate fechaCorte);

}
