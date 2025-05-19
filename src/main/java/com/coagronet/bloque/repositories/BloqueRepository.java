package com.coagronet.bloque.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.bloque.Bloque;

public interface BloqueRepository extends JpaRepository<Bloque, Long> {

	Optional<Bloque> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Bloque> findByEmpresaIdOrderByIdAsc(Long empresaId);

	List<Bloque> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}