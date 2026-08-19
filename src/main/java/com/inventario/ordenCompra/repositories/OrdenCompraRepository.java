package com.inventario.ordenCompra.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventario.ordenCompra.OrdenCompra;
import com.inventario.ordenCompra.dtos.OrdenCompraLookupDTO;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

	Page<OrdenCompra> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<OrdenCompra> findByIdAndEmpresaId(Long id, Long empresaId);

	Optional<OrdenCompra> findByPedidoIdAndEmpresaId(Long pedidoId, Long empresaId);

	@Query("""
			SELECT new com.inventario.ordenCompra.dtos.OrdenCompraLookupDTO(
			oc.id, oc.descripcion
			)
			FROM OrdenCompra oc
			WHERE oc.pedido.id = :pedidoId
			AND oc.empresa.id = :empresaId
			AND oc.estado.id = 1L
			""")
	List<OrdenCompraLookupDTO> findLookupByPedidoAndEmpresaAndEstadoId(@Param("pedidoId") Long pedidoId,
			@Param("empresaId") Long empresaId);

	@Query("SELECT o FROM OrdenCompra o WHERE o.id = :id AND o.estado.id = :estadoId")
	Optional<OrdenCompra> findByIdAndEstadoId(@Param("id") Long id, @Param("estadoId") Long estadoId);

}
