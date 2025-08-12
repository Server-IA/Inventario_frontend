package com.coagronet.tipoGenerico.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TipoGenericoRegistry {
    private final JdbcTemplate jdbcTemplate;

    private final Set<String> allowedTables = new HashSet<>();
    private final Map<String, String> prefixMap = new HashMap<>();
    private final Map<String, String> schemaMap = new HashMap<>();
    private final Map<String, Boolean> empresaIdMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        String sql = """
            SELECT table_schema, table_name
            FROM information_schema.tables
            WHERE table_schema IN ('iot', 'public','parqueadero')
            AND table_name LIKE 'tipo\\_%' ESCAPE '\\'
        """;

        jdbcTemplate.query(sql, rs -> {
            String schema = rs.getString("table_schema");
            String table = rs.getString("table_name");
            allowedTables.add(table);
            schemaMap.put(table, schema);

            String colSql = """
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = ? AND table_name = ? AND column_name LIKE '%\\_empresa_id' ESCAPE '\\'
            """;
            boolean hasEmpresaId = !jdbcTemplate.queryForList(colSql, schema, table).isEmpty();
            empresaIdMap.put(table, hasEmpresaId);

            String prefix = extractPrefixFromTable(schema, table);
            if (prefix != null && !prefix.isEmpty()) {
                prefixMap.put(table, prefix);
            }
        });
    }

    public boolean hasEmpresaId(String table) {
        return empresaIdMap.getOrDefault(table, false);
    }

    private String extractPrefixFromTable(String schema, String table) {
        String sql = """
            SELECT column_name
            FROM information_schema.columns
            WHERE table_schema = ?
              AND table_name = ?
              AND column_name LIKE '%\\_id' ESCAPE '\\'
              AND column_name NOT IN ('estado_id', 'empresa_id')
            LIMIT 1
        """;
        List<String> columnas = jdbcTemplate.queryForList(sql, String.class, schema, table);
        if (columnas.isEmpty()) return null;
        String col = columnas.get(0);
        
        return col.substring(0, col.length() - 3).toLowerCase();
    }

    public boolean isAllowed(String table) {
        return allowedTables.contains(table);
    }

    public String getPrefix(String table) {
        return prefixMap.getOrDefault(table, "");
    }

    public String getSchema(String table) {
        return schemaMap.getOrDefault(table, "public");
    }
}

