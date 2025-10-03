package com.coagronet.articuloKardex.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.articuloKardex.ArticuloKardex;

@Repository
public interface ArticuloKardexRepository extends JpaRepository<ArticuloKardex, Long> {

	interface RowCantidad {

		Long getPresentacionId();

		BigDecimal getCantidad();

	}

	Optional<ArticuloKardex> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<ArticuloKardex> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<ArticuloKardex> findByEmpresaIdAndKardexIdOrderByIdAsc(Long empresaId, Long kardexId);

	Optional<ArticuloKardex> findByidentificadorProductoAndEmpresaId(String identificadorProducto, Long empresaId);

	@Query("""
			  select ki.presentacionProducto.id as presentacionId,
			         coalesce(sum(ki.cantidad), 0)               as cantidad
			  from ArticuloKardex ki
			  join ki.kardex k
			  join k.tipoMovimiento tm
			  join tm.movimiento mv
			  where k.pedido.id = :pedidoId
			    and mv.id = :movId
			  group by ki.presentacionProducto.id
			""")
	List<RowCantidad> sumCantidadesKardexByPedidoAndMovimientoGroupByPresentacion(@Param("pedidoId") Long pedidoId,
			@Param("movId") Long movId);

	@Query("""
			  select case when count(ki.id) > 0 then true else false end
			  from ArticuloKardex ki
			  join ki.kardex k
			  join k.tipoMovimiento tm
			  join tm.movimiento mv
			  where k.pedido.id = :pedidoId
			    and mv.id = :movId
			""")
	boolean existsItemsByPedidoAndMovimiento(@Param("pedidoId") Long pedidoId, @Param("movId") Long movId);

}