package com.coagronet.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.inventario.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

	List<Inventario> findByEmpresaIdOrderByIdAsc(Long empresaId);

	Optional<Inventario> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Inventario> findByEmpresaIdAndFechaHoraBetweenOrderByFechaHoraAsc(Long empresaId, LocalDateTime inicio,
			LocalDateTime fin);

}
