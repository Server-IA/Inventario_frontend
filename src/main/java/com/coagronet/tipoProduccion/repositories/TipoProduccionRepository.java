package com.coagronet.tipoProduccion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoProduccion.TipoProduccion;

@Repository
public interface TipoProduccionRepository extends JpaRepository<TipoProduccion, Integer> {
    @Query("SELECT tp FROM TipoProduccion tp WHERE tp.id = :id AND tp.estado.id != :estadoId")
    <Optional>TipoProduccion findByIdAndEstadoIdNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

    @Query("SELECT tp FROM TipoProduccion tp WHERE tp.estado.id != :estadoId ORDER BY tp.id ASC")
    List<TipoProduccion> findByEstadoIdNotOrderByTipoIdAsc(@Param("estadoId") Integer estadoId);

    @Query("SELECT COUNT(tp) > 0 FROM TipoProduccion tp WHERE tp.id = :id AND tp.estado.id != :estadoId")
    boolean existsByIdAndEstadoIdNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);
}
