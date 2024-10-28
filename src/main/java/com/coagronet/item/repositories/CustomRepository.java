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

    public List<Item> findAllItems(String tableName) {
        Map<String, String> queries = appConfig.getQueries();
        if (queries == null) {
            throw new IllegalStateException("Configuration properties for queries are not initialized");
        }
        String sql = queries.get(tableName);

        System.out.println(tableName + " " + sql);

        Query query = entityManager.createNativeQuery(sql, "ItemMapping");
        return query.getResultList();
    }

}
