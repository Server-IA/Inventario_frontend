package com.coagronet.tipoMovimiento.reposritories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoMovimiento.TipoMovimiento;

public interface TipoMovimientoRepository extends JpaRepository<TipoMovimiento, Integer> {

    List<TipoMovimiento> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long empresaId,
            Integer estadoId);

    Optional<TipoMovimiento> findByIdAndEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEmpresaIdAndEstadoIdNot(Integer id,
            Long empresaId,
            Integer estadoId);

}
