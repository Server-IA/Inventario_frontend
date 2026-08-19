package com.inventario.productopresentacionstock.specs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.inventario.productopresentacionstock.ProductoPresentacionStock;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class ProductoPresentacionStockSpecs {

	private ProductoPresentacionStockSpecs() {
		// Prevenir instanciación
	}

	public static Specification<ProductoPresentacionStock> conFiltrosDinamicos(Long empresaId, Long almacenId,
			Long productoPresentacionId) {

		return (root, query, cb) -> {
			if (Long.class != query.getResultType()) {
				root.fetch("productoPresentacion", JoinType.LEFT);
			}

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("empresaId"), empresaId));

			if (almacenId != null) {
				predicates.add(cb.equal(root.get("almacenId"), almacenId));
			}

			if (productoPresentacionId != null) {
				predicates.add(cb.equal(root.get("productoPresentacionId"), productoPresentacionId));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
