package com.coagronet.reports.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
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
			    s.sed_nombre AS sede
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
			WHERE s.sed_empresa_id = :empresaId
			  AND s.sed_estado_id = :estadoActivo
			ORDER BY pais, departamento, municipio, sede
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

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public List<UbicacionReporteRow> findUbicacionesActivas(Long empresaId) {
		return jdbcTemplate.query(UBICACIONES_SQL, createParameters(empresaId), new UbicacionReporteRowMapper());
	}

	public List<ReporteKardexFiltroOpcionDTO> findCategoriasActivas(Long empresaId) {
		return jdbcTemplate.query(CATEGORIAS_SQL, createParameters(empresaId),
				(rs, rowNum) -> new ReporteKardexFiltroOpcionDTO(
						rs.getLong("id"),
						rs.getString("nombre"),
						null));
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
			String sede) {
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
					resultSet.getString("sede"));
		}
	}
}
