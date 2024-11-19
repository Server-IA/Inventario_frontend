package com.coagronet.tipoMovimiento.reposritories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoMovimiento.TipoMovimiento;

@Repository
public interface TipoMovimientoRepository extends JpaRepository<TipoMovimiento, Integer> {
    @Query("SELECT tm FROM TipoMovimiento tm WHERE tm.id = :id AND tm.estado.id != :estadoId")
    Optional<TipoMovimiento> findByIdAndEstadoIdNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

    @Query("SELECT tm FROM TipoMovimiento tm WHERE tm.estado.id != :estadoId ORDER BY tm.id ASC")
    List<TipoMovimiento> findByEstadoIdNotOrderByIdAsc(@Param("estadoId") Integer estadoId);

    @Query("SELECT COUNT(tm) > 0 FROM TipoMovimiento tm WHERE tm.id = :id AND tm.estado.id != :estadoId")
    boolean existsByIdAndEstadoIdNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);
}
