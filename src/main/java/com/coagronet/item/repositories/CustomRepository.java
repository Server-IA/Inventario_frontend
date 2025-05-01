package com.coagronet.item.repositories;

import java.util.List;
import java.util.Map;

import com.coagronet.empresa.Empresa;
import com.coagronet.user.User;
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
    private final AuthenticationService authenticationService;

    public CustomRepository(UserEmpresaService userEmpresaService, AuthenticationService authenticationService) {
        this.userEmpresaService = userEmpresaService;
        this.authenticationService = authenticationService;
    }

    public List<Item> findAllItems(String tableName, Long parentId) {
        Map<String, String> queries = appConfig.getQueries();
        if (queries == null) {
            throw new IllegalStateException("Configuration properties for queries are not initialized");
        }
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();
        String sql = queries.get(tableName);
        sql = sql.replace("$EMPRESA_ID$", String.valueOf(empresaId) );

        int start = sql.indexOf("$AND");
        int end = sql.indexOf("PARENT_ID$");

        if(start != -1 || end != -1){
            String parentText = sql.substring(start, end);

            if(parentId != null) {
                if(parentId != 0){
                    sql = sql.replace("PARENT_ID$", String.valueOf(parentId) );
                    sql = sql.replace("$AND", "and" );

                }else{
                    sql = sql.replace(parentText, "" );
                    sql = sql.replace("PARENT_ID$", "" );
                }
            }
        }

        Query query = entityManager.createNativeQuery(sql, "ItemMapping");

        System.out.println(tableName + " " + sql);
        return query.getResultList();
    }



}
