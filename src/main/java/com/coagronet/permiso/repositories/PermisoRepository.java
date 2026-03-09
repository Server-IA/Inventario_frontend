package com.coagronet.permiso.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.permiso.Permiso;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

	@Query("""
			    SELECT DISTINCT p.autoridad
			    FROM UsuarioRol ur
			    JOIN EmpresaRol er
			        ON er.empresa.id = ur.empresa.id
			       AND er.rol.id = ur.rol.id
			    JOIN RolPermiso rp
			        ON rp.empresaRol.id = er.id
			    JOIN Permiso p
			        ON p.id = rp.permiso.id
			    WHERE ur.user.id = :usuarioId
			      AND ur.empresa.id = :empresaId
			      AND ur.estado.id = 1
			      AND rp.estado.id = 1
			      AND p.estado.id = 1
			      AND (ur.finalizaContratoEn IS NULL OR ur.finalizaContratoEn > CURRENT_TIMESTAMP)
			""")
	List<String> findPermisosByUsuarioAndEmpresa(@Param("usuarioId") Long usuarioId,
			@Param("empresaId") Long empresaId);

	/**
	 * Obtiene todos los permisos de un módulo específico. Usado para mostrar en
	 * formulario y agrupar visualmente.
	 */
	@Query("""
			    SELECT p
			    FROM Permiso p
			    WHERE p.modulo.id = :moduloId
			      AND p.estado.id = 1
			    ORDER BY p.nombre
			""")
	List<Permiso> findPermisosByModuloId(@Param("moduloId") Long moduloId);

	/**
	 * Obtiene todos los permisos de múltiples módulos. Usado para asignar todos los
	 * permisos de módulos seleccionados a un rol.
	 */
	@Query("""
			    SELECT p
			    FROM Permiso p
			    WHERE p.modulo.id IN :modulosIds
			      AND p.estado.id = 1
			    ORDER BY p.modulo.nombre, p.nombre
			""")
	List<Permiso> findPermisosByModulosIds(@Param("modulosIds") List<Long> modulosIds);

	/**
	 * Obtiene módulos únicos (DISTINCT) con permisos activos, con paginación. Está
	 * optimizada para obtener módulos agrupados por página.
	 */
	@Query(value = """
			SELECT m.mod_id
			FROM public.modulo m
			WHERE m.mod_estado_id = 1
			  AND EXISTS (
			      SELECT 1
			      FROM public.permiso p
			      WHERE p.modulo_id = m.mod_id
			        AND p.estado_id = 1
			  )
			""", countQuery = """
			SELECT count(*)
			FROM public.modulo m
			WHERE m.mod_estado_id = 1
			  AND EXISTS (
			      SELECT 1
			      FROM public.permiso p
			      WHERE p.modulo_id = m.mod_id
			        AND p.estado_id = 1
			  )
			            """, nativeQuery = true)
	Page<Long> findDistinctModuloIds(Pageable pageable);

	@Query("""
			SELECT p
			FROM Permiso p
			JOIN FETCH p.modulo m
			LEFT JOIN FETCH p.metodo met
			WHERE m.id IN :moduloIds
			  AND p.estado.id = 1
			ORDER BY m.nombre
			""")
	List<Permiso> findPermisosConRelacionesByModuloIdIn(@Param("moduloIds") List<Long> moduloIds);

	/**
	 * Obtiene módulos únicos de subsistemas específicos sin paginación. Usado para listar
	 * todos los módulos de uno o varios subsistemas para selección UI.
	 */
	@Query(value = """
			SELECT DISTINCT p.modulo_id, m.mod_nombre
			FROM permiso p
			JOIN modulo m ON m.mod_id = p.modulo_id
			WHERE p.estado_id = 1
			  AND m.mod_estado_id = 1
			  AND m.mod_subsistema_id IN :subsistemaIds
			ORDER BY m.mod_nombre
			""", nativeQuery = true)
	List<Long> findDistinctModuloIdsBySubsistemas(@Param("subsistemaIds") List<Long> subsistemaIds);

	// Busca los permisos de una lista de módulos que son exclusivos para el admin
	List<Permiso> findByModuloIdInAndAdminEmpresaTrue(List<Long> moduloIds);

	/**
	 * Obtiene los permisos activos (y sus módulos y métodos asociados en una sola
	 * consulta) filtrando por los subsistemas solicitados y donde adminEmpresa sea true.
	 * El uso de JOIN FETCH evita el problema N+1.
	 */
	@Query("""
			    SELECT p
			    FROM Permiso p
			    JOIN FETCH p.modulo m
			    LEFT JOIN FETCH p.metodo met
			    WHERE m.subSistema.id IN :subsistemaIds
			      AND p.estado.id = 1
			      AND m.estado.id = 1
			      AND p.adminEmpresa = true
			    ORDER BY m.nombre ASC
			""")
	List<Permiso> findPermisosActivosAdminEmpresaBySubsistemas(@Param("subsistemaIds") List<Long> subsistemaIds);

	List<Permiso> findByIdInAndAdminEmpresaTrue(List<Long> ids);

}
