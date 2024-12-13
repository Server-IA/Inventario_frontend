package com.coagronet.ordenCompra.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.ordenCompra.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

        List<OrdenCompra> findByPedidoIdAndPedidoAlmacenSedeEmpresaIdAndEstadoIdNotOrderByIdAsc(
                        Integer pedidoId,
                        Long empresaId,
                        Integer estadoId);

        Optional<OrdenCompra> findByIdAndPedidoAlmacenSedeEmpresaId(
                        Long id,
                        Long empresaId);

        boolean existsByIdAndPedidoAlmacenSedeEmpresaId(
                        Long id,
                        Long empresaId);

        boolean existsByIdAndPedidoAlmacenSedeEmpresaIdAndEstadoIdNot(
                        Long id,
                        Long empresaId,
                        Integer estadoId);

}
