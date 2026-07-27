/*=============================================================================
 Nombre del archivo : ReporteKardexPreloadRepository.java
 Descripcion        : Repositorio de consultas para precargar filtros del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReporteKardexPreloadRepository {

	private static final Long ESTADO_ACTIVO = 1L;

	private static final String UBICACIONES_SQL = """
			SELECT DISTINCT
			    p.pai_id AS pais_id,
			    p.pai_nombre AS pais,
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
			FROM public.sede s
			JOIN public.municipio m
			  ON m.mun_id = s.sed_municipio_id
			 AND m.mun_estado_id = :estadoActivo
			JOIN public.departamento d
			  ON d.dep_id = m.mun_departamento_id
			 AND d.dep_estado_id = :estadoActivo
			JOIN public.pais p
			  ON p.pai_id = d.dep_pais_id
			 AND p.pai_estado_id = :estadoActivo
			LEFT JOIN public.bloque b
			  ON b.blo_sede_id = s.sed_id
			 AND b.blo_empresa_id = :empresaId
			 AND b.blo_estado_id = :estadoActivo
			LEFT JOIN public.espacio e
			  ON e.esp_bloque_id = b.blo_id
			 AND e.esp_empresa_id = :empresaId
			 AND e.esp_estado_id = :estadoActivo
			LEFT JOIN public.almacen a
			  ON a.alm_espacio_id = e.esp_id
			 AND a.alm_empresa_id = :empresaId
			 AND a.alm_estado_id = :estadoActivo
			WHERE s.sed_empresa_id = :empresaId
			  AND s.sed_estado_id = :estadoActivo
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
			JOIN public.producto p
			  ON p.pro_id = pp.prp_producto_id
			 AND p.pro_empresa_id = :empresaId
			 AND p.pro_estado_id = :estadoActivo
			WHERE pp.prp_empresa_id = :empresaId
			  AND pp.prp_estado_id = :estadoActivo
			  AND (:productoId IS NULL OR pp.prp_producto_id = :productoId)
			ORDER BY pp.prp_nombre
			""";

	private static final String PRODUCCIONES_SQL = """
			SELECT
			    pr.pro_id AS id,
			    pr.pro_nombre AS nombre,
			    pr.pro_espacio_id AS padre_id
			FROM public.produccion pr
			WHERE pr.pro_empresa_id = :empresaId
			  AND pr.pro_estado_id = :estadoActivo
			ORDER BY pr.pro_nombre
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public List<UbicacionReporteRow> findUbicacionesActivas(Long empresaId) {
		return jdbcTemplate.query(UBICACIONES_SQL, createParameters(empresaId), new UbicacionReporteRowMapper());
	}

	public List<ReporteKardexFiltroOpcionDTO> findCategoriasActivas(Long empresaId) {
		return jdbcTemplate.query(CATEGORIAS_SQL, createParameters(empresaId),
				(rs, rowNum) -> new ReporteKardexFiltroOpcionDTO(rs.getLong("id"), rs.getString("nombre"), null));
	}

	public List<ReporteKardexFiltroOpcionDTO> findProductosActivos(Long empresaId, Long categoriaId) {
		MapSqlParameterSource parameters = createParameters(empresaId)
			.addValue("categoriaId", categoriaId, Types.BIGINT);
		return jdbcTemplate.query(PRODUCTOS_SQL, parameters,
				(rs, rowNum) -> new ReporteKardexFiltroOpcionDTO(rs.getLong("id"), rs.getString("nombre"),
						rs.getLong("padre_id")));
	}

	public List<ReporteKardexFiltroOpcionDTO> findPresentacionesActivas(Long empresaId, Long productoId) {
		MapSqlParameterSource parameters = createParameters(empresaId)
			.addValue("productoId", productoId, Types.BIGINT);
		return jdbcTemplate.query(PRESENTACIONES_SQL, parameters,
				(rs, rowNum) -> new ReporteKardexFiltroOpcionDTO(rs.getLong("id"), rs.getString("nombre"),
						rs.getLong("padre_id")));
	}

	public List<ReporteKardexFiltroOpcionDTO> findProduccionesActivas(Long empresaId) {
		return jdbcTemplate.query(PRODUCCIONES_SQL, createParameters(empresaId),
				(rs, rowNum) -> new ReporteKardexFiltroOpcionDTO(rs.getLong("id"), rs.getString("nombre"),
						rs.getLong("padre_id")));
	}

	private MapSqlParameterSource createParameters(Long empresaId) {
		return new MapSqlParameterSource()
			.addValue("empresaId", empresaId)
			.addValue("estadoActivo", ESTADO_ACTIVO);
	}

	public record UbicacionReporteRow(
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

	private static final class UbicacionReporteRowMapper implements RowMapper<UbicacionReporteRow> {

		@Override
		public UbicacionReporteRow mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			return new UbicacionReporteRow(
					resultSet.getLong("pais_id"),
					resultSet.getString("pais"),
					resultSet.getLong("departamento_id"),
					resultSet.getString("departamento"),
					resultSet.getLong("municipio_id"),
					resultSet.getString("municipio"),
					resultSet.getLong("sede_id"),
					resultSet.getString("sede"),
					nullableLong(resultSet, "bloque_id"),
					resultSet.getString("bloque"),
					nullableLong(resultSet, "espacio_id"),
					resultSet.getString("espacio"),
					nullableLong(resultSet, "almacen_id"),
					resultSet.getString("almacen"));
		}

		private Long nullableLong(ResultSet resultSet, String columnName) throws SQLException {
			long value = resultSet.getLong(columnName);
			return resultSet.wasNull() ? null : value;
		}
	}
}
