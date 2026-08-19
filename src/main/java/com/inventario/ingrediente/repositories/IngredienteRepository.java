package com.inventario.ingrediente.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.ingrediente.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

	Optional<Ingrediente> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Ingrediente> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
