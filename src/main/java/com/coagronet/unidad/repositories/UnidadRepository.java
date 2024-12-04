package com.coagronet.unidad.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.unidad.Unidad;

public interface UnidadRepository extends JpaRepository<Unidad, Integer> {

    Optional<Unidad> findByIdAndEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estado);

    List<Unidad> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

}
