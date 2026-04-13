package com.coagronet.kardex.repositories;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.coagronet.kardex.Kardex;

import jakarta.persistence.criteria.JoinType;

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
            }

            var predicates = cb.conjunction();

            if (fechaInicio != null && fechaFin != null) {
                predicates.getExpressions().add(cb.between(root.get("fechaHora"), fechaInicio, fechaFin));
            }
            if (tipoMovimientoId != null) {
                predicates.getExpressions().add(cb.equal(root.get("tipoMovimiento").get("id"), tipoMovimientoId));
            }
            if (estadoId != null) {
                predicates.getExpressions().add(cb.equal(root.get("estado").get("id"), estadoId));
            }

            return predicates;
        };
    }
}
