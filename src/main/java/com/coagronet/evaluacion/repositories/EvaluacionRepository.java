package com.coagronet.evaluacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.evaluacion.Evaluacion;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    List<Evaluacion> findByTipoEvaluacionId(
            Integer tipoEvaluacionId);

    List<Evaluacion> findByTipoEvaluacionIdAndEstadoIdNot(
            Integer tipoEvaluacionId,
            Integer estadoId);

    boolean existsByIdAndEstadoIdNot(
            Integer id,
            Integer estadoId);
}
