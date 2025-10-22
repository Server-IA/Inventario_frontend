package com.coagronet.menu.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.Modulo;

public interface MenuRepository extends Repository<Modulo, Long> {

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
