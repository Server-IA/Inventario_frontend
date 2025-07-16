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

    public static final String urlVersion= "v1";
    


    public List<TipoGenericoDTO> findAll(String table) {

        if (!registry.isAllowed(table)) {
            throw new BadRequestException("Tabla no permitida o no válida: " + table);
        }
        
        String schema = registry.getSchema(table);
        String pre = registry.getPrefix(table);
        String fullTable = schema + "." + table;

        String sql = String.format("""
            SELECT %s_id, %s_nombre, %s_descripcion, %s_estado_id, %s_empresa_id
            FROM %s
            WHERE %s_empresa_id = ?
            ORDER BY %s_id ASC
        """, pre, pre, pre, pre, pre, fullTable, pre, pre);

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), empresaId);
    }

    public Optional<TipoGenericoDTO> findById(String table, Long id) {
        
        if (!registry.isAllowed(table)) {
            throw new BadRequestException("Tabla no permitida o no válida: " + table);
        }

        String schema = registry.getSchema(table);
        String pre = registry.getPrefix(table);
        String fullTable = schema + "." + table;

        String sql = String.format("""
            SELECT %s_id, %s_nombre, %s_descripcion, %s_estado_id, %s_empresa_id
            FROM %s
            WHERE %s_id = ? AND %s_empresa_id = ?
        """, pre, pre, pre, pre, pre, fullTable, pre, pre);

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        Object[] params = new Object[]{id, empresaId};

        List<TipoGenericoDTO> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), params);
        return resultados.stream().findFirst();
    }

    private TipoGenericoDTO mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong(1);
        String nombre = rs.getString(2);
        String descripcion = rs.getString(3);
        Long estadoId = rs.getLong(4);
        Long empresaId = rs.getLong(5);
        if (rs.wasNull()) empresaId = null;

        return TipoGenericoDTO.builder()
            .id(id)
            .nombre(nombre)
            .descripcion(descripcion)
            .estadoId(estadoId)
            .empresaId(empresaId)
            .build();
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

        dto.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        String sequenceName = fullTable + "_" + pre + "_id_seq";
        Long id = jdbcTemplate.queryForObject("SELECT nextval(?)", Long.class, sequenceName);

        String sql = String.format("""
            INSERT INTO %s (%s_id, %s_nombre, %s_descripcion, %s_estado_id, %s_empresa_id)
            VALUES (?, ?, ?, ?, ?)
        """, fullTable, pre, pre, pre, pre, pre);

        Object[] params = new Object[]{id, dto.getNombre(), dto.getDescripcion(), dto.getEstadoId(), dto.getEmpresaId()};
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

        dto.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        String sql = String.format("""
            UPDATE %s
            SET %s_nombre = ?, %s_descripcion = ?, %s_estado_id = ?, %s_empresa_id = ?
            WHERE %s_id = ?
        """, fullTable, pre, pre, pre, pre, pre);
        Object[] params = new Object[]{dto.getNombre(), dto.getDescripcion(), dto.getEstadoId(), dto.getEmpresaId(), id};

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

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        String sql = String.format("""
            DELETE FROM %s WHERE %s_id = ? AND %s_empresa_id = ?
        """, fullTable, pre, pre);

        jdbcTemplate.update(sql, id, empresaId);
    }
}



    
