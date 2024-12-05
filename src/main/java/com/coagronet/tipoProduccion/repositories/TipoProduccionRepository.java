package com.coagronet.tipoProduccion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoProduccion.TipoProduccion;

public interface TipoProduccionRepository extends JpaRepository<TipoProduccion, Integer> {

    List<TipoProduccion> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long empresaId,
            Integer estadoId);

    Optional<TipoProduccion> findByIdAndEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEmpresaIdAndEstadoIdNot(Integer id,
            Long empresaId,
            Integer estadoId);

}
