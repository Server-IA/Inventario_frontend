package com.coagronet.pedidoItem.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.pedidoItem.PedidoItem;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    Optional<PedidoItem> findByIdAndPedidoAlmacenSedeEmpresaId(
            Long id,
            Long empresaId);

    Page<PedidoItem> findByPedidoIdAndPedidoAlmacenSedeEmpresaIdAndEstadoIdNot(
            Integer pedidoId,
            Long empresaId,
            Integer estadoId,
            Pageable pageable);

    boolean existsByIdAndPedidoAlmacenSedeEmpresaId(
            Long id,
            Long empresaId);

}
