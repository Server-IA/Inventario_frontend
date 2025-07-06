package com.coagronet.criterioEvaluacion.repositirories;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Long> {

    List<CriterioEvaluacion> findByEmpresaIdOrderByIdAsc(Long empresaId);

    Optional<CriterioEvaluacion> findByIdAndEmpresaId(Long id, Long empresaId);
}
