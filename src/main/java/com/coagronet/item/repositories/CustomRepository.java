package com.coagronet.item.repositories;

import java.util.List;
import java.util.Map;

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

    public List<Item> findAllItems(String tableName, int page, int size) {
        Map<String, String> queries = appConfig.getQueries();
        if (queries == null || queries.get(tableName) == null) {
            throw new IllegalStateException("No hay consulta configurada para esta tabla");
        }
        String sql = queries.get(tableName);

        Query query = entityManager.createNativeQuery(sql, "ItemMapping");
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return (List<Item>) query.getResultList();
    }

}
