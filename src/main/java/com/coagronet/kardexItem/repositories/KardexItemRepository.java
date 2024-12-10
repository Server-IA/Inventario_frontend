package com.coagronet.kardexItem.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.kardexItem.KardexItem;

public interface KardexItemRepository extends JpaRepository<KardexItem, Integer> {

    Optional<KardexItem> findByIdAndKardexAlmacenSedeEmpresaId(
            Integer id,
            Long empresaId);

    Page<KardexItem> findByKardexAlmacenSedeEmpresaIdAndEstadoIdNot(
            Long empresaId,
            Integer estadoId,
            Pageable pageable);

    boolean existsByIdAndKardexAlmacenSedeEmpresaId(
            Integer id,
            Long empresaId);

}
