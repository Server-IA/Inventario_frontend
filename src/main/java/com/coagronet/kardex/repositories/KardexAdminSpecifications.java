package com.coagronet.kardex.repositories;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.coagronet.kardex.KardexAdminView;

public class KardexAdminSpecifications {
    public static Specification<KardexAdminView> conFiltros(
            OffsetDateTime fechaInicio, OffsetDateTime fechaFin, Long tipoMovimientoId, Long estadoId) {

        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (fechaInicio != null && fechaFin != null) {
                predicates.getExpressions().add(cb.between(root.get("fechaHora"), fechaInicio, fechaFin));
            }
            if (tipoMovimientoId != null) {
                predicates.getExpressions().add(cb.equal(root.get("tipoMovimientoId"), tipoMovimientoId));
            }
            if (estadoId != null) {
                predicates.getExpressions().add(cb.equal(root.get("estadoId"), estadoId));
            }
            return predicates;
        };
    }
}