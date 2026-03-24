package com.coagronet.productopresentacionstock.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.coagronet.productopresentacionstock.ProductoPresentacionStock;

@Repository
public interface ProductoPresentacionStockRepository
		extends JpaRepository<ProductoPresentacionStock, Long>, JpaSpecificationExecutor<ProductoPresentacionStock> {

	@EntityGraph(attributePaths = { "productoPresentacion", "almacen" })
	Page<ProductoPresentacionStock> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	/**
	 * El stock debe resolverse por Almacén, respetando la restricción UNIQUE de BD.
	 */
	@EntityGraph(attributePaths = { "productoPresentacion" })
	Optional<ProductoPresentacionStock> findByEmpresaIdAndAlmacenIdAndProductoPresentacionId(Long empresaId,
			Long almacenId, Long productoPresentacionId);

}