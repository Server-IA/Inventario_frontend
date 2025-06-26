package com.coagronet.item.repositories;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.coagronet.infrastructure.configuration.AppConfig;
import com.coagronet.item.models.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AppConfig appConfig;

    private final UserEmpresaService userEmpresaService;

    public CustomRepository(UserEmpresaService userEmpresaService, AuthenticationService authenticationService) {
        this.userEmpresaService = userEmpresaService;
    }

    public List<Item> findAllItems(String tableName, Long parentId) {
        Map<String, String> queries = appConfig.getQueries();
        if (queries == null) {
            throw new IllegalStateException("Configuration properties for queries are not initialized");
        }
        String sql = queries.get(tableName);
        sql = sql.replace("$EMPRESA_ID$", String.valueOf(userEmpresaService.getEmpresaIdFromCurrentRequest()));

        int start = sql.indexOf("$AND");
        int end = sql.indexOf("PARENT_ID$");

        if (start != -1 || end != -1) {
            String parentText = sql.substring(start, end);

            if (parentId != null) {
                if (parentId != 0) {
                    sql = sql.replace("PARENT_ID$", String.valueOf(parentId));
                    sql = sql.replace("$AND", "and");

                } else {
                    sql = sql.replace(parentText, "");
                    sql = sql.replace("PARENT_ID$", "");
                }
            }
        }

        Query query = entityManager.createNativeQuery(sql);

        // Map the result manually
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new Item(
                        row[0] instanceof Number n ? n.longValue() : Long.parseLong(row[0].toString()),
                        row[1] != null ? row[1].toString() : null))
                .collect(Collectors.toList());
    }
}