package com.coagronet.ordenCompra.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.ordenCompra.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

	Page<OrdenCompra> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<OrdenCompra> findByIdAndEmpresaId(Long id, Long empresaId);
	Optional<OrdenCompra> findByPedidoIdAndEmpresaId(Long pedidoId, Long empresaId);

}
