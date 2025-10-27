package com.coagronet.productopresentacionstock.repositories;

import com.coagronet.productopresentacionstock.ProductoPresentacionStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoPresentacionStockRepository extends JpaRepository<ProductoPresentacionStock,Long> {

    Page<ProductoPresentacionStock> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

    Optional<ProductoPresentacionStock>
    findByEmpresaIdAndProductoPresentacionId(Long empresaId, Long productoPresentacionId);
}
