package com.coagronet.criterioEvaluacion.repositirories;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Long> {

    List<CriterioEvaluacion> findByTipoEvaluacionIdAndEstadoIdNotOrderByIdAsc(Long tipoEvaluacionId,Long estadoId);

    Optional<CriterioEvaluacion> findByIdAndEstadoIdNot(Long id, Long estadoId);

    boolean existsByIdAndEstadoIdNot(Long id, Long estadoId);

    boolean existsByTipoEvaluacionIdAndEstadoIdNot(Long tipoEvaluacionId, Long estadoId); // Nuevo método
}
