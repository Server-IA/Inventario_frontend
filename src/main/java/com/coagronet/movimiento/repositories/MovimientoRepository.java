package com.coagronet.movimiento.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.movimiento.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

	Optional<Movimiento> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Movimiento> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
