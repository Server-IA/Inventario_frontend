package com.inventario.criterioEvaluacion.repositirories;

import com.inventario.criterioEvaluacion.CriterioEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Long> {

	Page<CriterioEvaluacion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<CriterioEvaluacion> findByIdAndEmpresaId(Long id, Long empresaId);

}
