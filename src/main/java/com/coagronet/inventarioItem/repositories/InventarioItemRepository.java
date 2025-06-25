package com.coagronet.inventarioItem.repositories;

import com.coagronet.inventarioItem.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioItemRepository extends JpaRepository<InventarioItem, Long> {

    List<InventarioItem> findByEmpresaIdOrderByIdAsc(Long empresaId);

    Optional<InventarioItem> findByIdAndEmpresaId(Long id, Long empresaId);
}
