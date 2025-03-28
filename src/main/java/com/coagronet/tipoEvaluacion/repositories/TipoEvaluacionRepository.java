package com.coagronet.tipoEvaluacion.repositories;

import com.coagronet.tipoEvaluacion.TipoEvaluacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoEvaluacionRepository extends JpaRepository<TipoEvaluacion, Integer> {

        List<TipoEvaluacion> findByEstadoIdNotOrderByIdAsc(
                        Integer estadoId);

        Optional<TipoEvaluacion> findById(
                        Integer id);

        boolean existsById(
                        Integer id);

}
