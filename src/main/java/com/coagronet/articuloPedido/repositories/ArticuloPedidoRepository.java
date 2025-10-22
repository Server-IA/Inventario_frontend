package com.coagronet.articuloPedido.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.articuloPedido.ArticuloPedido;

@Repository
public interface ArticuloPedidoRepository extends JpaRepository<ArticuloPedido, Long> {

	interface RowCantidad {

		Long getPresentacionId();

		Double getCantidad();

	}

	Optional<ArticuloPedido> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<ArticuloPedido> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<ArticuloPedido> findByEmpresaIdAndPedidoIdOrderByIdAsc(Long empresaId, Long pedidoId);

	@Query("""
			  select pi.presentacionProducto.id as presentacionId,
			         coalesce(sum(pi.cantidad), 0) as cantidad
			  from ArticuloPedido pi
			  where pi.pedido.id = :pedidoId
			  group by pi.presentacionProducto.id
			""")
	List<RowCantidad> sumCantidadesPedidasGroupByPresentacion(@Param("pedidoId") Long pedidoId);

}