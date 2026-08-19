package com.inventario.inventarioItem.repositories;

import com.inventario.inventarioItem.InventarioItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioItemRepository extends JpaRepository<InventarioItem, Long> {

	Page<InventarioItem> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<InventarioItem> findByIdAndEmpresaId(Long id, Long empresaId);

}
