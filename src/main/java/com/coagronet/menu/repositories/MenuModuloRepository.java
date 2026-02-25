package com.coagronet.menu.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.Modulo;

/**
 * Repositorio de lectura para construir el menú a partir de los módulos disponibles.
 * <p>
 * Usa una consulta nativa para traer filas planas (proyección {@link SubModuloRow}) que incluyen datos del subsistema y
 * del módulo, filtradas por empresa, estado, tipo de aplicación y rol.
 * </p>
 *
 * <p>
 * <strong>Notas:</strong> Se ordena por nombre de subsistema y luego de módulo para mantener estabilidad en la UI.
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public interface MenuModuloRepository extends Repository<Modulo, Long> {

	@Query(value = """
						SELECT
			    s.sub_nombre    AS subNombre,
			    s.sub_icon      AS subIcon,
			    m.mod_nombre_id AS modNombreId,
			    m.mod_nombre    AS modNombre,
			    m.mod_url       AS modUrl,
			    m.mod_icon      AS modIcon
			FROM public.modulo m
			JOIN public.subsistema s
			     ON s.sub_id = m.mod_subsistema_id
			JOIN public.modulo_empresa me
			     ON me.moe_modulo_id = m.mod_id
			WHERE me.moe_empresa_id = :empresaId
			  AND me.moe_estado_id  = 1
			  AND m.mod_estado_id   = 1
			  AND m.mod_tipo_aplicacion_id = :tipoAppId
			  AND EXISTS (
			      SELECT 1
			      FROM public.permiso p
			      JOIN public.rol_permiso rp
			           ON rp.permiso_id = p.id
			      JOIN public.empresa_rol er
			           ON er.id = rp.empresa_rol_id
			      WHERE p.modulo_id = m.mod_id
			        AND er.empresa_id = :empresaId
			        AND er.rol_id = :rolId
			  )
			ORDER BY s.sub_nombre ASC, m.mod_nombre ASC;
						""", nativeQuery = true)
	List<SubModuloRow> findSubmodulosByEmpresaTipoAppAndRolId(@Param("empresaId") Long empresaId,
			@Param("tipoAppId") Integer tipoAppId, @Param("rolId") Integer rolId);

	/**
	 * Consulta la base de datos para recuperar los módulos que no están asociados a una empresa específica.
	 * <p>
	 * Ejecuta una consulta JPQL que selecciona las entidades {@link Modulo} cuyo identificador no se encuentra en la
	 * tabla de relación <code>ModuloEmpresa</code> para el <code>empresaId</code> proporcionado. Los resultados se
	 * ordenan ascendentemente por el nombre del subsistema y posteriormente por el nombre del módulo.
	 * </p>
	 *
	 * @param empresaId El identificador único de la empresa para la cual se filtran los módulos ya asignados.
	 * @return Una lista de entidades {@link Modulo} disponibles (no asignadas) para la empresa indicada.
	 */
	@Query("SELECT m FROM Modulo m " + "JOIN FETCH m.subSistema s " + "WHERE m.id NOT IN (" + "   SELECT me.modulo.id "
			+ "   FROM ModuloEmpresa me " + "   WHERE me.empresa.id = :empresaId" + ") "
			+ "ORDER BY s.nombre ASC, m.nombre ASC")
	List<Modulo> findModulosNoAsignados(@Param("empresaId") Long empresaId);

}
