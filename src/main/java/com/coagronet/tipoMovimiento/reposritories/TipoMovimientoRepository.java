package com.coagronet.tipoMovimiento.reposritories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoMovimiento.TipoMovimiento;

public interface TipoMovimientoRepository extends JpaRepository<TipoMovimiento, Integer> {
    List<TipoMovimiento> findByEstadoNot(Integer estado, Sort sort);
}
