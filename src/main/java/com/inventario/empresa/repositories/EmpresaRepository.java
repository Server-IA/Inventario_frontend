/*=============================================================================
 Nombre del archivo : EmpresaRepository.java
 Descripcion        : Repositorio JPA para la persistencia y consulta de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2024-08-16 | 1.0.0   | yourusername         | Creacion del archivo.                                                                                                              |
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Se agrega consulta con relaciones requeridas para el detalle de empresa de la HU-043.3.                                            |
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Se agrega consulta paginada, filtrada y con alcance por empresa para la HU-043.2.                                                  |
 | 2026-07-27 | 1.1.1   | JUAN DIAZ            | Correccion del tipado de filtros de texto opcionales en PostgreSQL para evitar llamadas LOWER sobre parametros bytea.             |
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Se agregan validaciones de unicidad de identificacion y correo para la HU-043.1.                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.empresa.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventario.empresa.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

	Page<Empresa> findByEstadoNot(Integer estado, Pageable pageable);

	@Query(value = "SELECT e.emp_logo FROM empresa e WHERE e.emp_logo_hash = ?1", nativeQuery = true)
	String findLogoByHash(String logoHash);

	@Query(value = "SELECT emp_logo_hash FROM empresa WHERE emp_id = ?1", nativeQuery = true)
	String findLogoHashByEmpresaId(Long empresaId);

	@Query("SELECT e FROM Empresa e WHERE e.id = :id AND e.estado.id = :estadoId")
	Optional<Empresa> findByIdAndEstadoId(@Param("id") Long id, @Param("estadoId") Long estadoId);

	@Query("""
			SELECT e
			FROM Empresa e
			JOIN FETCH e.tipoIdentificacion
			JOIN FETCH e.persona
			LEFT JOIN FETCH e.estado
			WHERE e.id = :id
			""")
	Optional<Empresa> buscarDetallePorId(@Param("id") Long id);

	@Query(
			value = """
					SELECT e
					FROM Empresa e
					JOIN FETCH e.tipoIdentificacion ti
					LEFT JOIN FETCH e.estado es
					WHERE (:empresaId IS NULL OR e.id = :empresaId)
					  AND (:tipoIdentificacionId IS NULL OR ti.id = :tipoIdentificacionId)
					  AND (:identificacion = ''
					       OR LOWER(e.identificacion) LIKE LOWER(CONCAT('%', :identificacion, '%')))
					  AND (:nombre = ''
					       OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
					  AND (:correo = ''
					       OR LOWER(e.correo) LIKE LOWER(CONCAT('%', :correo, '%')))
					  AND (:estadoId IS NULL OR es.id = :estadoId)
					""",
			countQuery = """
					SELECT COUNT(e)
					FROM Empresa e
					JOIN e.tipoIdentificacion ti
					LEFT JOIN e.estado es
					WHERE (:empresaId IS NULL OR e.id = :empresaId)
					  AND (:tipoIdentificacionId IS NULL OR ti.id = :tipoIdentificacionId)
					  AND (:identificacion = ''
					       OR LOWER(e.identificacion) LIKE LOWER(CONCAT('%', :identificacion, '%')))
					  AND (:nombre = ''
					       OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
					  AND (:correo = ''
					       OR LOWER(e.correo) LIKE LOWER(CONCAT('%', :correo, '%')))
					  AND (:estadoId IS NULL OR es.id = :estadoId)
					""")
	Page<Empresa> buscarEmpresas(
			@Param("empresaId") Long empresaId,
			@Param("tipoIdentificacionId") Long tipoIdentificacionId,
			@Param("identificacion") String identificacion,
			@Param("nombre") String nombre,
			@Param("correo") String correo,
			@Param("estadoId") Long estadoId,
			Pageable pageable);
	boolean existsByIdentificacionIgnoreCase(String identificacion);

	boolean existsByCorreoIgnoreCase(String correo);

}
