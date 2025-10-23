package com.coagronet.menu.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.Modulo;

/**
 * Repositorio de lectura para construir el menú a partir de los módulos
 * disponibles.
 * <p>
 * Usa una consulta nativa para traer filas planas (proyección
 * {@link SubModuloRow}) que incluyen datos del subsistema y del módulo,
 * filtradas por empresa, estado, tipo de aplicación y rol.
 * </p>
 *
 * <p>
 * <strong>Notas:</strong> Se ordena por nombre de subsistema y luego de módulo
 * para mantener estabilidad en la UI.
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public interface MenuModuloRepository extends Repository<Modulo, Long> {

	/**
	 * Recupera las filas de submódulos visibles para una empresa, tipo de
	 * aplicación y rol.
	 * <p>
	 * Criterios clave:
	 * <ul>
	 * <li>{@code me.moe_empresa_id = :empresaId}</li>
	 * <li>{@code me.moe_estado_id = 1} y {@code m.mod_estado_id = 1} (activos)</li>
	 * <li>{@code m.mod_tipo_aplicacion_id = :tipoAppId}</li>
	 * <li>Rol: {@code m.mod_rol_id IS NULL} o
	 * {@code :roleName = ANY(m.mod_rol_id)}</li>
	 * </ul>
	 * </p>
	 *
	 * @param empresaId ID de la empresa del contexto
	 * @param tipoAppId ID interno del tipo de aplicación (p. ej. {@code 1 = WEB},
	 *                  {@code 2 = MOVIL})
	 * @param roleName  nombre del rol actual del usuario (debe coincidir con el
	 *                  almacenado en BD)
	 * @return lista ordenada por nombre de subsistema y nombre de módulo
	 */
	@Query(value = """
			SELECT
			  s.sub_nombre   AS subNombre,
			  s.sub_icon     AS subIcon,
			  m.mod_nombre_id AS modNombreId,
			  m.mod_nombre   AS modNombre,
			  m.mod_url      AS modUrl,
			  m.mod_icon     AS modIcon
			FROM public.modulo m
			JOIN public.subsistema s
			     ON s.sub_id = m.mod_subsistema_id
			JOIN public.modulo_empresa me
			     ON me.moe_modulo_id = m.mod_id
			WHERE me.moe_empresa_id = :empresaId
			  AND me.moe_estado_id  = 1
			  AND m.mod_estado_id   = 1
			  AND m.mod_tipo_aplicacion_id = :tipoAppId
			  AND (
			        m.mod_rol_id IS NULL
			        OR :roleName = ANY(m.mod_rol_id)
			      )
			ORDER BY s.sub_nombre ASC, m.mod_nombre ASC
			""", nativeQuery = true)
	List<SubModuloRow> findSubmodulosByEmpresaTipoAppAndRol(@Param("empresaId") Long empresaId,
			@Param("tipoAppId") Integer tipoAppId, @Param("roleName") String roleName);

}
