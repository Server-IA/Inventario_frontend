/*=============================================================================
 Nombre del archivo : KardexSpecifications.java
 Descripcion        : Especificaciones JPA para la consulta dinámica y filtrado
                      de Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-22 | 0.4.0   | JUAN JOSE CASTRO     | Adición de un LEFT JOIN     |
 |            |         |                      | (fetch) para la relación    |
 |            |         |                      | username, optimizando la    |
 |            |         |                      | carga de datos en consultas.|
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.kardex.repositories;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.coagronet.kardex.Kardex;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class KardexSpecifications {

    public static Specification<Kardex> conFiltros(
            OffsetDateTime fechaInicio,
            OffsetDateTime fechaFin,
            Long tipoMovimientoId,
            Long estadoId) {

        return (root, query, cb) -> {
            // Prevención de N+1 (Fetch Joins para @ManyToOne)
            if (Long.class != query.getResultType()) { // Evitar fetch en consultas count() paginadas
                root.fetch("almacen", JoinType.INNER);
                root.fetch("tipoMovimiento", JoinType.INNER);
                root.fetch("estado", JoinType.INNER);
                root.fetch("empresa", JoinType.LEFT);
                root.fetch("clienteProveedor", JoinType.LEFT);
                root.fetch("produccion", JoinType.LEFT);
                root.fetch("username", JoinType.LEFT);
            }

            // 1. Use a standard ArrayList to hold your predicates
            List<Predicate> predicates = new ArrayList<>();

            // 2. Add conditions directly to the ArrayList
            if (fechaInicio != null && fechaFin != null) {
                predicates.add(cb.between(root.get("fechaHora"), fechaInicio, fechaFin));
            }
            if (tipoMovimientoId != null) {
                predicates.add(cb.equal(root.get("tipoMovimiento").get("id"), tipoMovimientoId));
            }
            if (estadoId != null) {
                predicates.add(cb.equal(root.get("estado").get("id"), estadoId));
            }

            // 3. Return the combined predicates using cb.and()
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
