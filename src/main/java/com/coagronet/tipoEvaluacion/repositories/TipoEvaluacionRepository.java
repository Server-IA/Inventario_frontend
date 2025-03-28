package com.coagronet.tipoEvaluacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoEvaluacion.TipoEvaluacion;

@Repository
public interface TipoEvaluacionRepository extends JpaRepository<TipoEvaluacion, Integer> {

    List<TipoEvaluacion> findByEstadoIdNotOrderByIdAsc(
            Integer estadoId);

    boolean existsByIdAndEstadoIdNot(
            Integer id,
            Integer estadoId);

}
