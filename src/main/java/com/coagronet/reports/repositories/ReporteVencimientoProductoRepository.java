package com.coagronet.reports.repositories;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoEstado;
import com.coagronet.reports.dtos.ReporteVencimientoProductoFiltroDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReporteVencimientoProductoRepository {

	private static final Long ESTADO_ACTIVO = 1L;

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
			FROM public.almacen a
			JOIN public.espacio e
			  ON e.esp_id = a.alm_espacio_id
			 AND e.esp_estado_id = :estadoActivo
			 AND e.esp_empresa_id = :empresaId
			JOIN public.bloque b
			  ON b.blo_id = e.esp_bloque_id
			 AND b.blo_estado_id = :estadoActivo
			 AND b.blo_empresa_id = :empresaId
			JOIN public.sede s
			  ON s.sed_id = b.blo_sede_id
			 AND s.sed_estado_id = :estadoActivo
			 AND s.sed_empresa_id = :empresaId
			JOIN public.municipio m
			  ON m.mun_id = s.sed_municipio_id
			JOIN public.departamento d
			  ON d.dep_id = m.mun_departamento_id
			JOIN public.pais pa
			  ON pa.pai_id = d.dep_pais_id
			WHERE a.alm_empresa_id = :empresaId
			  AND a.alm_estado_id = :estadoActivo
			ORDER BY pais, departamento, municipio, sede, bloque, espacio, almacen
			""";

	private static final String CATEGORIAS_SQL = """
			SELECT
			    pc.prc_id AS id,
			    pc.prc_nombre AS nombre
			FROM public.producto_categoria pc
			WHERE pc.prc_empresa_id = :empresaId
			  AND pc.prc_estado_id = :estadoActivo
			ORDER BY pc.prc_nombre
			""";

	private static final String PRODUCTOS_SQL = """
			SELECT
			    p.pro_id AS id,
			    p.pro_nombre AS nombre,
			    p.pro_producto_categoria_id AS padre_id
			FROM public.producto p
			WHERE p.pro_empresa_id = :empresaId
			  AND p.pro_estado_id = :estadoActivo
			  AND (:categoriaId IS NULL OR p.pro_producto_categoria_id = :categoriaId)
			ORDER BY p.pro_nombre
			""";

	private static final String PRESENTACIONES_SQL = """
			SELECT
			    pp.prp_id AS id,
			    pp.prp_nombre AS nombre,
			    pp.prp_producto_id AS padre_id
			FROM public.producto_presentacion pp
			WHERE pp.prp_empresa_id = :empresaId
			  AND pp.prp_estado_id = :estadoActivo
			  AND (:productoId IS NULL OR pp.prp_producto_id = :productoId)
			ORDER BY pp.prp_nombre
			""";

	private static final String RESULTADOS_SQL = """
			SELECT
			    ki.kai_id AS kardex_item_id,
			    p.pro_nombre AS producto,
			    ki.kai_fecha_vencimiento AS fecha_vencimiento,
			    ki.kai_cantidad AS cantidad,
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
			FROM public.kardex_item ki
			JOIN public.kardex k
			  ON k.kar_id = ki.kai_kardex_id
			 AND k.kar_empresa_id = :empresaId
			 AND k.kar_estado_id = :estadoActivo
			JOIN public.producto_presentacion pp
			  ON pp.prp_id = ki.kai_producto_presentacion_id
			 AND pp.prp_empresa_id = :empresaId
			 AND pp.prp_estado_id = :estadoActivo
			JOIN public.producto p
			  ON p.pro_id = pp.prp_producto_id
			 AND p.pro_empresa_id = :empresaId
			 AND p.pro_estado_id = :estadoActivo
			JOIN public.producto_categoria pc
			  ON pc.prc_id = p.pro_producto_categoria_id
			 AND pc.prc_empresa_id = :empresaId
			 AND pc.prc_estado_id = :estadoActivo
			JOIN public.almacen a
			  ON a.alm_id = k.kar_almacen_id
			 AND a.alm_empresa_id = :empresaId
			 AND a.alm_estado_id = :estadoActivo
			JOIN public.espacio e
			  ON e.esp_id = a.alm_espacio_id
			 AND e.esp_empresa_id = :empresaId
			 AND e.esp_estado_id = :estadoActivo
			JOIN public.bloque b
			  ON b.blo_id = e.esp_bloque_id
			 AND b.blo_empresa_id = :empresaId
			 AND b.blo_estado_id = :estadoActivo
			JOIN public.sede s
			  ON s.sed_id = b.blo_sede_id
			 AND s.sed_empresa_id = :empresaId
			 AND s.sed_estado_id = :estadoActivo
			JOIN public.municipio m
			  ON m.mun_id = s.sed_municipio_id
			JOIN public.departamento d
			  ON d.dep_id = m.mun_departamento_id
			JOIN public.pais pa
			  ON pa.pai_id = d.dep_pais_id
			WHERE ki.kai_empresa_id = :empresaId
			  AND ki.kai_estado_id = :estadoActivo
			  AND ki.kai_fecha_vencimiento IS NOT NULL
			  AND ki.kai_fecha_vencimiento BETWEEN :fechaInicio AND :fechaFin
			  AND (:paisId IS NULL OR pa.pai_id = :paisId)
			  AND (:departamentoId IS NULL OR d.dep_id = :departamentoId)
			  AND (:municipioId IS NULL OR m.mun_id = :municipioId)
			  AND (:sedeId IS NULL OR s.sed_id = :sedeId)
			  AND (:bloqueId IS NULL OR b.blo_id = :bloqueId)
			  AND (:espacioId IS NULL OR e.esp_id = :espacioId)
			  AND (:almacenId IS NULL OR a.alm_id = :almacenId)
			  AND (:categoriaId IS NULL OR pc.prc_id = :categoriaId)
			  AND (:productoId IS NULL OR p.pro_id = :productoId)
			  AND (:presentacionId IS NULL OR pp.prp_id = :presentacionId)
			  AND (
			      :estado = 'TODOS'
			      OR (:estado = 'VENCIDO' AND ki.kai_fecha_vencimiento <= :fechaGeneracion)
			      OR (:estado = 'PROXIMO_A_VENCER' AND ki.kai_fecha_vencimiento > :fechaGeneracion)
			  )
			ORDER BY ki.kai_fecha_vencimiento ASC, p.pro_nombre ASC
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public List<UbicacionVencimientoRow> findUbicacionesActivas(Long empresaId) {
		return jdbcTemplate.query(UBICACIONES_SQL, createBaseParameters(empresaId), new UbicacionVencimientoRowMapper());
	}

	public List<ReporteKardexFiltroOpcionDTO> findCategoriasActivas(Long empresaId) {
		return jdbcTemplate.query(CATEGORIAS_SQL, createBaseParameters(empresaId),
				(rs, rowNum) -> new ReporteKardexFiltroOpcionDTO(
						rs.getLong("id"),
						rs.getString("nombre"),
						null));
	}

	public List<ReporteKardexFiltroOpcionDTO> findProductosActivos(Long empresaId, Long categoriaId) {
		MapSqlParameterSource parameters = createBaseParameters(empresaId)
			.addValue("categoriaId", categoriaId, Types.BIGINT);
		return jdbcTemplate.query(PRODUCTOS_SQL, parameters, new FiltroOpcionConPadreMapper());
	}

	public List<ReporteKardexFiltroOpcionDTO> findPresentacionesActivas(Long empresaId, Long productoId) {
		MapSqlParameterSource parameters = createBaseParameters(empresaId)
			.addValue("productoId", productoId, Types.BIGINT);
		return jdbcTemplate.query(PRESENTACIONES_SQL, parameters, new FiltroOpcionConPadreMapper());
	}

	public List<ResultadoVencimientoRow> findResultados(
			Long empresaId,
			ReporteVencimientoProductoFiltroDTO filtro,
			LocalDate fechaGeneracion) {
		return jdbcTemplate.query(
				RESULTADOS_SQL,
				createFiltroParameters(empresaId, filtro, fechaGeneracion),
				new ResultadoVencimientoRowMapper());
	}

	private MapSqlParameterSource createBaseParameters(Long empresaId) {
		return new MapSqlParameterSource()
			.addValue("empresaId", empresaId, Types.BIGINT)
			.addValue("estadoActivo", ESTADO_ACTIVO, Types.BIGINT);
	}

	private MapSqlParameterSource createFiltroParameters(
			Long empresaId,
			ReporteVencimientoProductoFiltroDTO filtro,
			LocalDate fechaGeneracion) {
		return createBaseParameters(empresaId)
			.addValue("paisId", filtro.paisId(), Types.BIGINT)
			.addValue("departamentoId", filtro.departamentoId(), Types.BIGINT)
			.addValue("municipioId", filtro.municipioId(), Types.BIGINT)
			.addValue("sedeId", filtro.sedeId(), Types.BIGINT)
			.addValue("bloqueId", filtro.bloqueId(), Types.BIGINT)
			.addValue("espacioId", filtro.espacioId(), Types.BIGINT)
			.addValue("almacenId", filtro.almacenId(), Types.BIGINT)
			.addValue("categoriaId", filtro.categoriaId(), Types.BIGINT)
			.addValue("productoId", filtro.productoId(), Types.BIGINT)
			.addValue("presentacionId", filtro.presentacionId(), Types.BIGINT)
			.addValue("fechaInicio", filtro.fechaInicio(), Types.DATE)
			.addValue("fechaFin", filtro.fechaFin(), Types.DATE)
			.addValue("fechaGeneracion", fechaGeneracion, Types.DATE)
			.addValue("estado", ReporteVencimientoProductoEstado.normalize(filtro.estado()).name(), Types.VARCHAR);
	}

	public record UbicacionVencimientoRow(
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

	public record ResultadoVencimientoRow(
			Long kardexItemId,
			String producto,
			LocalDate fechaVencimiento,
			BigDecimal cantidad,
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

	private static final class UbicacionVencimientoRowMapper implements RowMapper<UbicacionVencimientoRow> {

		@Override
		public UbicacionVencimientoRow mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			return new UbicacionVencimientoRow(
					resultSet.getLong("pais_id"),
					resultSet.getString("pais"),
					resultSet.getLong("departamento_id"),
					resultSet.getString("departamento"),
					resultSet.getLong("municipio_id"),
					resultSet.getString("municipio"),
					resultSet.getLong("sede_id"),
					resultSet.getString("sede"),
					resultSet.getLong("bloque_id"),
					resultSet.getString("bloque"),
					resultSet.getLong("espacio_id"),
					resultSet.getString("espacio"),
					resultSet.getLong("almacen_id"),
					resultSet.getString("almacen"));
		}

	}

	private static final class FiltroOpcionConPadreMapper implements RowMapper<ReporteKardexFiltroOpcionDTO> {

		@Override
		public ReporteKardexFiltroOpcionDTO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			return new ReporteKardexFiltroOpcionDTO(
					resultSet.getLong("id"),
					resultSet.getString("nombre"),
					resultSet.getLong("padre_id"));
		}

	}

	private static final class ResultadoVencimientoRowMapper implements RowMapper<ResultadoVencimientoRow> {

		@Override
		public ResultadoVencimientoRow mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			return new ResultadoVencimientoRow(
					resultSet.getLong("kardex_item_id"),
					resultSet.getString("producto"),
					resultSet.getDate("fecha_vencimiento").toLocalDate(),
					resultSet.getBigDecimal("cantidad"),
					resultSet.getLong("pais_id"),
					resultSet.getString("pais"),
					resultSet.getLong("departamento_id"),
					resultSet.getString("departamento"),
					resultSet.getLong("municipio_id"),
					resultSet.getString("municipio"),
					resultSet.getLong("sede_id"),
					resultSet.getString("sede"),
					resultSet.getLong("bloque_id"),
					resultSet.getString("bloque"),
					resultSet.getLong("espacio_id"),
					resultSet.getString("espacio"),
					resultSet.getLong("almacen_id"),
					resultSet.getString("almacen"));
		}

	}

}
