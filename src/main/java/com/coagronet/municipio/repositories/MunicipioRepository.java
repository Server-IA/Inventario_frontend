/*=============================================================================
 Nombre del archivo : MunicipioRepository.java
 Descripcion        : Repositorio JPA para consultas de municipios.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.       |
 | 2026-05-29 | 1.1.0   | JUAN DIAZ            | Ajustes aplicados por PR.   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
package com.coagronet.municipio.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.municipio.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

	List<Municipio> findByDepartamentoIdOrderByIdAsc(Long departamentoId);

	@Query("""
			SELECT m
			FROM Municipio m
			WHERE m.departamento.id = :departamentoId
			  AND (:nombre IS NULL OR :nombre = '' OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
			  AND (:codigo IS NULL OR m.codigo = :codigo)
			  AND (:acronimo IS NULL OR :acronimo = '' OR LOWER(m.acronimo) LIKE LOWER(CONCAT('%', :acronimo, '%')))
			  AND (:estadoId IS NULL OR m.estado.id = :estadoId)
			ORDER BY m.id ASC
			""")
	List<Municipio> findByDepartamentoIdWithFilters(@Param("departamentoId") Long departamentoId,
			@Param("nombre") String nombre, @Param("codigo") Integer codigo, @Param("acronimo") String acronimo,
			@Param("estadoId") Long estadoId);

	List<Municipio> findByDepartamentoIdAndEstadoIdNotOrderByIdAsc(Long departamentoId, Long estadoId);

	// Compatibilidad para modulos que todavia tienen el campo empresa antes de que
	// municipio se volviera global.
	default Optional<Municipio> findByIdAndEmpresaId(Long id, Long empresaId) {
		return findById(id);
	}

	boolean existsByIdAndDepartamentoId(Long id, Long departamentoId);

	boolean existsByDepartamentoIdAndNombreIgnoreCase(Long departamentoId, String nombre);

	boolean existsByDepartamentoIdAndNombreIgnoreCaseAndIdNot(Long departamentoId, String nombre, Long id);

	boolean existsByDepartamentoIdAndCodigo(Long departamentoId, Integer codigo);

	boolean existsByDepartamentoIdAndCodigoAndIdNot(Long departamentoId, Integer codigo, Long id);

	boolean existsByDepartamentoIdAndAcronimoIgnoreCase(Long departamentoId, String acronimo);

	boolean existsByDepartamentoIdAndAcronimoIgnoreCaseAndIdNot(Long departamentoId, String acronimo, Long id);

}
