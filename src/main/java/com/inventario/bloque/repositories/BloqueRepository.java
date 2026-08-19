package com.inventario.bloque.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.bloque.Bloque;

public interface BloqueRepository extends JpaRepository<Bloque, Long> {

	Optional<Bloque> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Bloque> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<Bloque> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}