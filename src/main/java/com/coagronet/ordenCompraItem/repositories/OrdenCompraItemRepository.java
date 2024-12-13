package com.coagronet.ordenCompraItem.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.ordenCompraItem.OrdenCompraItem;

public interface OrdenCompraItemRepository extends JpaRepository<OrdenCompraItem, Integer> {

    Optional<OrdenCompraItem> findByIdAndOrdenCompraPedidoAlmacenSedeEmpresaId(
            Integer id,
            Long empresaId);

    List<OrdenCompraItem> findByOrdenCompraIdAndOrdenCompraPedidoAlmacenSedeEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long ordenCompraId,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndOrdenCompraPedidoAlmacenSedeEmpresaId(
            Integer id,
            Long empresaId);

}
