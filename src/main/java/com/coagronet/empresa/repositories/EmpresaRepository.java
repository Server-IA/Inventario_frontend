/*=============================================================================
 Nombre del archivo : EmpresaRepository.java
 Descripcion        : Repositorio JPA para la persistencia y consulta de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2024-08-16 | 1.0.0   | yourusername         | Creacion del archivo.                                                                                                              |
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Se agregan validaciones de unicidad de identificacion y correo para la HU-043.1.                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.empresa.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

	Page<Empresa> findByEstadoNot(Integer estado, Pageable pageable);

	@Query(value = "SELECT e.emp_logo FROM empresa e WHERE e.emp_logo_hash = ?1", nativeQuery = true)
	String findLogoByHash(String logoHash);

	@Query(value = "SELECT emp_logo_hash FROM empresa WHERE emp_id = ?1", nativeQuery = true)
	String findLogoHashByEmpresaId(Long empresaId);

	@Query("SELECT e FROM Empresa e WHERE e.id = :id AND e.estado.id = :estadoId")
	Optional<Empresa> findByIdAndEstadoId(@Param("id") Long id, @Param("estadoId") Long estadoId);

	boolean existsByIdentificacionIgnoreCase(String identificacion);

	boolean existsByCorreoIgnoreCase(String correo);

}
