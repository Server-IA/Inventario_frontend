package com.coagronet.tipoGenerico.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.tipoGenerico.dtos.TipoGenericoDTO;
import com.coagronet.tipoGenerico.registry.TipoGenericoRegistry;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoGenericoService {

    private final JdbcTemplate jdbcTemplate;
    private final TipoGenericoRegistry registry;
    private final UserEmpresaService userEmpresaService;
    private final EstadoRepository estadoRepository;

    public List<TipoGenericoDTO> findAll(String table) {
        if (!registry.isAllowed(table)) {
            throw new BadRequestException("Tabla no permitida o no válida: " + table);
        }
        String schema = registry.getSchema(table);
        String pre = registry.getPrefix(table);
        String fullTable = schema + "." + table;
        boolean hasEmpresaId = registry.hasEmpresaId(table);

        String sql;
        Object[] params;
        if (hasEmpresaId) {
            sql = String.format("""
                SELECT %s_id, %s_nombre, %s_descripcion, %s_estado_id, %s_empresa_id
                FROM %s
                WHERE %s_empresa_id = ?
                ORDER BY %s_id ASC
            """, pre, pre, pre, pre, pre, fullTable, pre, pre);
            Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
            params = new Object[]{empresaId};
        } else {
            sql = String.format("""
                SELECT %s_id, %s_nombre, %s_descripcion, %s_estado_id
                FROM %s
                ORDER BY %s_id ASC
            """, pre, pre, pre, pre, fullTable, pre);
            params = new Object[]{};
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs, pre, hasEmpresaId), params);
    }

    public Optional<TipoGenericoDTO> findById(String table, Long id) {
        if (!registry.isAllowed(table)) {
            throw new BadRequestException("Tabla no permitida o no válida: " + table);
        }
        String schema = registry.getSchema(table);
        String pre = registry.getPrefix(table);
        String fullTable = schema + "." + table;
        boolean hasEmpresaId = registry.hasEmpresaId(table);

        String sql;
        Object[] params;
        if (hasEmpresaId) {
            sql = String.format("""
                SELECT %s_id, %s_nombre, %s_descripcion, %s_estado_id, %s_empresa_id
                FROM %s
                WHERE %s_id = ? AND %s_empresa_id = ?
            """, pre, pre, pre, pre, pre, fullTable, pre, pre);
            Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
            params = new Object[]{id, empresaId};
        } else {
            sql = String.format("""
                SELECT %s_id, %s_nombre, %s_descripcion, %s_estado_id
                FROM %s
                WHERE %s_id = ?
            """, pre, pre, pre, pre, fullTable, pre);
            params = new Object[]{id};
        }
        List<TipoGenericoDTO> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs, pre, hasEmpresaId), params);
        return resultados.stream().findFirst();
    }

    private TipoGenericoDTO mapRow(ResultSet rs, String prefix, boolean hasEmpresaId) throws SQLException {
        TipoGenericoDTO dto = new TipoGenericoDTO();
        dto.setId(rs.getLong(prefix + "_id"));
        dto.setNombre(rs.getString(prefix + "_nombre"));
        dto.setDescripcion(rs.getString(prefix + "_descripcion"));
        dto.setEstadoId(rs.getObject(prefix + "_estado_id") != null ? rs.getLong(prefix + "_estado_id") : null);
        if (hasEmpresaId) {
            dto.setEmpresaId(rs.getObject(prefix + "_empresa_id") != null ? rs.getLong(prefix + "_empresa_id") : null);
        }
        return dto;
    }

    public TipoGenericoDTO create(String table, TipoGenericoDTO dto) {
        if (!registry.isAllowed(table)) {
            throw new BadRequestException("Tabla no permitida o no válida: " + table);
        }
        estadoRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido. "));
        String schema = registry.getSchema(table);
        String pre = registry.getPrefix(table);
        String fullTable = schema + "." + table;
        boolean hasEmpresaId = registry.hasEmpresaId(table);

        String sequenceName = fullTable + "_" + pre + "_id_seq";
        String nextValSql = "SELECT nextval('" + sequenceName + "')";
        Long id = jdbcTemplate.queryForObject(nextValSql, Long.class);

        String sql;
        Object[] params;
        if (hasEmpresaId) {
            dto.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
            sql = String.format("""
                INSERT INTO %s (%s_id, %s_nombre, %s_descripcion, %s_estado_id, %s_empresa_id)
                VALUES (?, ?, ?, ?, ?)
            """, fullTable, pre, pre, pre, pre, pre);
            params = new Object[]{id, dto.getNombre(), dto.getDescripcion(), dto.getEstadoId(), dto.getEmpresaId()};
        } else {
            sql = String.format("""
                INSERT INTO %s (%s_id, %s_nombre, %s_descripcion, %s_estado_id)
                VALUES (?, ?, ?, ?)
            """, fullTable, pre, pre, pre, pre);
            params = new Object[]{id, dto.getNombre(), dto.getDescripcion(), dto.getEstadoId()};
        }
        jdbcTemplate.update(sql, params);
        dto.setId(id);
        return dto;
    }

    public void update(String table, Long id, TipoGenericoDTO dto) {
        if (!registry.isAllowed(table)) {
            throw new BadRequestException("Tabla no permitida o no válida: " + table);
        }
        estadoRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido. "));
        if (findById(table, id).isEmpty()) {
            throw new BadRequestException("No se encontró el registro con id: " + id + " o es inválido para la empresa.");
        }
        String schema = registry.getSchema(table);
        String pre = registry.getPrefix(table);
        String fullTable = schema + "." + table;
        boolean hasEmpresaId = registry.hasEmpresaId(table);

        String sql;
        Object[] params;
        if (hasEmpresaId) {
            dto.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
            sql = String.format("""
                UPDATE %s
                SET %s_nombre = ?, %s_descripcion = ?, %s_estado_id = ?, %s_empresa_id = ?
                WHERE %s_id = ?
            """, fullTable, pre, pre, pre, pre, pre);
            params = new Object[]{dto.getNombre(), dto.getDescripcion(), dto.getEstadoId(), dto.getEmpresaId(), id};
        } else {
            sql = String.format("""
                UPDATE %s
                SET %s_nombre = ?, %s_descripcion = ?, %s_estado_id = ?
                WHERE %s_id = ?
            """, fullTable, pre, pre, pre, pre);
            params = new Object[]{dto.getNombre(), dto.getDescripcion(), dto.getEstadoId(), id};
        }
        jdbcTemplate.update(sql, params);
    }

    public void delete(String table, Long id) {
        if (!registry.isAllowed(table)) {
            throw new BadRequestException("Tabla no permitida o no válida: " + table);
        }
        if (findById(table, id).isEmpty()){
            throw new BadRequestException("No se encontró el registro con id: " + id + " o es inválido para la empresa.");
        }
        String schema = registry.getSchema(table);
        String pre = registry.getPrefix(table);
        String fullTable = schema + "." + table;
        boolean hasEmpresaId = registry.hasEmpresaId(table);

        String sql;
        Object[] params;
        if (hasEmpresaId) {
            Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
            sql = String.format("""
                DELETE FROM %s WHERE %s_id = ? AND %s_empresa_id = ?
            """, fullTable, pre, pre);
            params = new Object[]{id, empresaId};
        } else {
            sql = String.format("""
                DELETE FROM %s WHERE %s_id = ?
            """, fullTable, pre);
            params = new Object[]{id};
        }
        jdbcTemplate.update(sql, params);
    }
}