/*=============================================================================
 Nombre del archivo : ReportePedidoAuditoriaRepository.java
 Descripcion        : Repositorio para registrar la auditoria de reportes de pedido.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.repositories;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReportePedidoAuditoriaRepository {

	private static final String INSERT_SQL = """
			INSERT INTO public.reporte_pedido_auditoria (
			    rpa_generacion_id,
			    rpa_empresa_id,
			    rpa_usuario_id,
			    rpa_usuario,
			    rpa_fecha_hora,
			    rpa_pedido_id,
			    rpa_formato
			) VALUES (
			    :generacionId,
			    :empresaId,
			    :usuarioId,
			    :usuario,
			    :fechaHora,
			    :pedidoId,
			    :formato
			)
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public void registrar(
			UUID generacionId,
			Long empresaId,
			Long usuarioId,
			String usuario,
			OffsetDateTime fechaHora,
			List<Long> pedidoIds,
			String formato) {
		MapSqlParameterSource[] batch = pedidoIds.stream()
			.map(pedidoId -> new MapSqlParameterSource()
				.addValue("generacionId", generacionId, Types.OTHER)
				.addValue("empresaId", empresaId, Types.BIGINT)
				.addValue("usuarioId", usuarioId, Types.BIGINT)
				.addValue("usuario", usuario, Types.VARCHAR)
				.addValue("fechaHora", fechaHora, Types.TIMESTAMP_WITH_TIMEZONE)
				.addValue("pedidoId", pedidoId, Types.BIGINT)
				.addValue("formato", formato, Types.VARCHAR))
			.toArray(MapSqlParameterSource[]::new);
		jdbcTemplate.batchUpdate(INSERT_SQL, batch);
	}

}
