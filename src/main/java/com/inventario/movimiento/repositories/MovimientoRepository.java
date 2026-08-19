package com.inventario.movimiento.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.movimiento.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

}
