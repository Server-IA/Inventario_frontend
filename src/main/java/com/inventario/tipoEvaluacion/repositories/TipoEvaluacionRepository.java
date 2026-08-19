package com.inventario.tipoEvaluacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.tipoEvaluacion.TipoEvaluacion;

@Repository
public interface TipoEvaluacionRepository extends JpaRepository<TipoEvaluacion, Long> {

	List<TipoEvaluacion> findAllByOrderByIdAsc();

}
