package com.coagronet.evaluacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.evaluacion.Evaluacion;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

        Optional<Evaluacion> findByIdAndEmpresaId(
                        Long id,
                        Long empresaId);

        List<Evaluacion> findByEmpresaIdAndTipoEvaluacionIdOrderByIdAsc(
                        Long empresaId,
                        Long tipoEvaluacionId);

        List<Evaluacion> findByEmpresaIdOrderByIdAsc(
                        Long empresaId);
}
