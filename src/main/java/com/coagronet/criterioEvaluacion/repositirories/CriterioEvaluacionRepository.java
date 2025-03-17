package com.coagronet.criterioEvaluacion.repositirories;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Integer> {

    List<CriterioEvaluacion> findByTipoEvaluacionIdAndEstadoIdNotOrderByIdAsc(Integer tipoEvaluacionId,Integer estadoId);

    Optional<CriterioEvaluacion> findByIdAndEstadoIdNot(Integer id, Integer estadoId);

    boolean existsByIdAndEstadoIdNot(Integer id, Integer estadoId);

    boolean existsByTipoEvaluacionIdAndEstadoIdNot(Integer tipoEvaluacionId, Integer estadoId); // Nuevo método
}
