package com.inventario.grupo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.grupo.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

	Optional<Grupo> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Grupo> findByEmpresaIdOrderByIdAsc(Long empresaId);

	List<Grupo> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}