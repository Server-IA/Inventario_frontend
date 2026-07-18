package com.coagronet.reports.repositories;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.coagronet.reports.dtos.ReportePedidoFiltroDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReportePedidoRepository {

	private static final String UBICACIONES_SQL = """
			SELECT DISTINCT
			    pa.pai_id AS pais_id,
			    pa.pai_nombre AS pais,
			    d.dep_id AS departamento_id,
			    d.dep_nombre AS departamento,
			    m.mun_id AS municipio_id,
			    m.mun_nombre AS municipio,
			    s.sed_id AS sede_id,
			    s.sed_nombre AS sede,
			    b.blo_id AS bloque_id,
			    b.blo_nombre AS bloque,
			    e.esp_id AS espacio_id,
			    e.esp_nombre AS espacio,
			    a.alm_id AS almacen_id,
			    a.alm_nombre AS almacen
			FROM public.pedido p
			JOIN public.almacen a ON a.alm_id = p.ped_almacen_id AND a.alm_empresa_id = :empresaId
			JOIN public.espacio e ON e.esp_id = a.alm_espacio_id AND e.esp_empresa_id = :empresaId
			JOIN public.bloque b ON b.blo_id = e.esp_bloque_id AND b.blo_empresa_id = :empresaId
			JOIN public.sede s ON s.sed_id = b.blo_sede_id AND s.sed_empresa_id = :empresaId
			JOIN public.municipio m ON m.mun_id = s.sed_municipio_id
			JOIN public.departamento d ON d.dep_id = m.mun_departamento_id
			JOIN public.pais pa ON pa.pai_id = d.dep_pais_id
			WHERE p.ped_empresa_id = :empresaId
			ORDER BY pais, departamento, municipio, sede, bloque, espacio, almacen
			""";

	private static final String PEDIDOS_SQL = """
			SELECT p.ped_id AS id
			FROM public.pedido p
			WHERE p.ped_empresa_id = :empresaId
			ORDER BY p.ped_fecha_hora DESC, p.ped_id DESC
			""";

	private static final String ESTADOS_SQL = """
			SELECT DISTINCT est.est_id AS id, est.est_nombre AS nombre
			FROM public.pedido p
			JOIN public.estado est ON est.est_id = p.ped_estado_id
			WHERE p.ped_empresa_id = :empresaId
			ORDER BY est.est_nombre
			""";

	private static final String RESULTADOS_BASE_SQL = """
			SELECT
			    p.ped_id AS pedido_id,
			    p.ped_fecha_hora AS pedido_fecha,
			    est.est_nombre AS pedido_estado,
			    em.emp_nombre AS empresa,
			    em.emp_correo AS correo,
			    em.emp_contacto AS contacto,
			    em.emp_logo AS logo_archivo,
			    TRIM(CONCAT_WS(' ', per.per_nombre, per.per_apellido)) AS responsable,
			    pa.pai_id AS pais_id,
			    d.dep_id AS departamento_id,
			    m.mun_id AS municipio_id,
			    m.mun_nombre AS municipio,
			    s.sed_id AS sede_id,
			    s.sed_nombre AS sede,
			    b.blo_id AS bloque_id,
			    b.blo_nombre AS bloque,
			    e.esp_id AS espacio_id,
			    e.esp_nombre AS espacio,
			    a.alm_id AS almacen_id,
			    a.alm_nombre AS almacen,
			    pi.pei_id AS pedido_item_id,
			    pp.prp_id AS presentacion_id,
			    prod.pro_nombre AS producto,
			    pi.pei_cantidad AS cantidad,
			    u.uni_nombre AS unidad
			FROM public.pedido p
			JOIN public.estado est ON est.est_id = p.ped_estado_id
			JOIN public.empresa em ON em.emp_id = p.ped_empresa_id
			LEFT JOIN public.persona per ON per.per_id = em.emp_persona_id
			JOIN public.almacen a ON a.alm_id = p.ped_almacen_id AND a.alm_empresa_id = :empresaId
			JOIN public.espacio e ON e.esp_id = a.alm_espacio_id AND e.esp_empresa_id = :empresaId
			JOIN public.bloque b ON b.blo_id = e.esp_bloque_id AND b.blo_empresa_id = :empresaId
			JOIN public.sede s ON s.sed_id = b.blo_sede_id AND s.sed_empresa_id = :empresaId
			JOIN public.municipio m ON m.mun_id = s.sed_municipio_id
			JOIN public.departamento d ON d.dep_id = m.mun_departamento_id
			JOIN public.pais pa ON pa.pai_id = d.dep_pais_id
			LEFT JOIN public.pedido_item pi
			  ON pi.pei_pedido_id = p.ped_id
			 AND pi.pei_empresa_id = :empresaId
			LEFT JOIN public.producto_presentacion pp
			  ON pp.prp_id = pi.pei_producto_presentacion_id
			 AND pp.prp_empresa_id = :empresaId
			LEFT JOIN public.producto prod
			  ON prod.pro_id = pp.prp_producto_id
			 AND prod.pro_empresa_id = :empresaId
			LEFT JOIN public.unidad u ON u.uni_id = pp.prp_unidad_id
			WHERE p.ped_empresa_id = :empresaId
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public List<UbicacionPedidoRow> findUbicaciones(Long empresaId) {
		return jdbcTemplate.query(UBICACIONES_SQL, baseParameters(empresaId), new UbicacionPedidoRowMapper());
	}

	public List<ReporteKardexFiltroOpcionDTO> findPedidos(Long empresaId) {
		return jdbcTemplate.query(PEDIDOS_SQL, baseParameters(empresaId),
				(rs, rowNum) -> {
					Long id = rs.getLong("id");
					return new ReporteKardexFiltroOpcionDTO(id, id.toString(), null);
				});
	}

	public List<ReporteKardexFiltroOpcionDTO> findEstados(Long empresaId) {
		return jdbcTemplate.query(ESTADOS_SQL, baseParameters(empresaId),
				(rs, rowNum) -> new ReporteKardexFiltroOpcionDTO(
						rs.getLong("id"), rs.getString("nombre"), null));
	}

	public List<PedidoReporteRow> findResultados(Long empresaId, ReportePedidoFiltroDTO filtro) {
		StringBuilder sql = new StringBuilder(RESULTADOS_BASE_SQL);
		MapSqlParameterSource parameters = baseParameters(empresaId);

		if (!filtro.pedidoIds().isEmpty()) {
			sql.append(" AND p.ped_id IN (:pedidoIds)");
			parameters.addValue("pedidoIds", filtro.pedidoIds());
		}
		appendLongFilter(sql, parameters, "p.ped_estado_id", "estadoId", filtro.estadoId());
		if (filtro.fechaInicio() != null) {
			sql.append(" AND p.ped_fecha_hora >= :fechaInicio");
			parameters.addValue("fechaInicio", filtro.fechaInicio().atStartOfDay(), Types.TIMESTAMP);
		}
		if (filtro.fechaFin() != null) {
			sql.append(" AND p.ped_fecha_hora < :fechaFinExclusiva");
			parameters.addValue("fechaFinExclusiva", filtro.fechaFin().plusDays(1).atStartOfDay(), Types.TIMESTAMP);
		}
		appendLongFilter(sql, parameters, "pa.pai_id", "paisId", filtro.paisId());
		appendLongFilter(sql, parameters, "d.dep_id", "departamentoId", filtro.departamentoId());
		appendLongFilter(sql, parameters, "m.mun_id", "municipioId", filtro.municipioId());
		appendLongFilter(sql, parameters, "s.sed_id", "sedeId", filtro.sedeId());
		appendLongFilter(sql, parameters, "b.blo_id", "bloqueId", filtro.bloqueId());
		appendLongFilter(sql, parameters, "e.esp_id", "espacioId", filtro.espacioId());
		appendLongFilter(sql, parameters, "a.alm_id", "almacenId", filtro.almacenId());
		sql.append(" ORDER BY p.ped_fecha_hora ASC, p.ped_id ASC, pi.pei_id ASC");

		return jdbcTemplate.query(sql.toString(), parameters, new PedidoReporteRowMapper());
	}

	private MapSqlParameterSource baseParameters(Long empresaId) {
		return new MapSqlParameterSource().addValue("empresaId", empresaId, Types.BIGINT);
	}

	private void appendLongFilter(
			StringBuilder sql,
			MapSqlParameterSource parameters,
			String column,
			String parameter,
			Long value) {
		if (value != null) {
			sql.append(" AND ").append(column).append(" = :").append(parameter);
			parameters.addValue(parameter, value, Types.BIGINT);
		}
	}

	public record UbicacionPedidoRow(
			Long paisId,
			String pais,
			Long departamentoId,
			String departamento,
			Long municipioId,
			String municipio,
			Long sedeId,
			String sede,
			Long bloqueId,
			String bloque,
			Long espacioId,
			String espacio,
			Long almacenId,
			String almacen) {
	}

	public record PedidoReporteRow(
			Long pedidoId,
			LocalDateTime pedidoFecha,
			String pedidoEstado,
			String empresa,
			String correo,
			String contacto,
			String logoArchivo,
			String responsable,
			String municipio,
			String sede,
			String bloque,
			String espacio,
			String almacen,
			Long pedidoItemId,
			Long presentacionId,
			String producto,
			BigDecimal cantidad,
			String unidad) {
	}

	private static final class UbicacionPedidoRowMapper implements RowMapper<UbicacionPedidoRow> {

		@Override
		public UbicacionPedidoRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new UbicacionPedidoRow(
					rs.getLong("pais_id"), rs.getString("pais"),
					rs.getLong("departamento_id"), rs.getString("departamento"),
					rs.getLong("municipio_id"), rs.getString("municipio"),
					rs.getLong("sede_id"), rs.getString("sede"),
					rs.getLong("bloque_id"), rs.getString("bloque"),
					rs.getLong("espacio_id"), rs.getString("espacio"),
					rs.getLong("almacen_id"), rs.getString("almacen"));
		}

	}

	private static final class PedidoReporteRowMapper implements RowMapper<PedidoReporteRow> {

		@Override
		public PedidoReporteRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			Timestamp timestamp = rs.getTimestamp("pedido_fecha");
			return new PedidoReporteRow(
					rs.getLong("pedido_id"),
					timestamp == null ? null : timestamp.toLocalDateTime(),
					rs.getString("pedido_estado"),
					rs.getString("empresa"),
					rs.getString("correo"),
					rs.getString("contacto"),
					rs.getString("logo_archivo"),
					rs.getString("responsable"),
					rs.getString("municipio"),
					rs.getString("sede"),
					rs.getString("bloque"),
					rs.getString("espacio"),
					rs.getString("almacen"),
					nullableLong(rs, "pedido_item_id"),
					nullableLong(rs, "presentacion_id"),
					rs.getString("producto"),
					rs.getBigDecimal("cantidad"),
					rs.getString("unidad"));
		}

		private Long nullableLong(ResultSet rs, String column) throws SQLException {
			long value = rs.getLong(column);
			return rs.wasNull() ? null : value;
		}

	}

}
