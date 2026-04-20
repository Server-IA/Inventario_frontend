package com.coagronet.articuloKardex.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;
import com.coagronet.articuloKardex.dtos.KardexItemResponseDto;

@Repository
public interface ArticuloKardexRepository
		extends JpaRepository<ArticuloKardex, Long>, JpaSpecificationExecutor<ArticuloKardex> {

	interface RowCantidad {

		Long getPresentacionId();

		BigDecimal getCantidad();

	}

	Optional<ArticuloKardex> findByidentificadorProductoAndEmpresaId(String identificadorProducto, Long empresaId);

	@EntityGraph(attributePaths = { "presentacionProducto", "estado" })
	Optional<ArticuloKardex> findByIdAndEmpresaId(Long id, Long empresaId);

	@EntityGraph(attributePaths = { "presentacionProducto", "estado" })
	Page<ArticuloKardex> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	@EntityGraph(attributePaths = { "presentacionProducto", "estado", "responsable" })
	List<ArticuloKardex> findByEmpresaIdAndKardexIdOrderByIdAsc(Long empresaId, Long kardexId);

	Optional<ArticuloKardex> findByIdentificadorProductoAndEmpresaId(String identificadorProducto, Long empresaId);

	@Query("""
			  select ki.presentacionProducto.id as presentacionId,
			         coalesce(sum(ki.cantidad), 0) as cantidad
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

	List<ArticuloKardex> findByEmpresaIdAndKardex_OrdenCompra_IdOrderByIdAsc(Long empresaId, Long ordenCompraId);

	@Query("""
			    SELECT new com.coagronet.articuloKardex.dtos.ArticuloKardexDTO(
			        a.id, a.cantidad, a.precio, a.fechaVencimiento, a.identificadorProducto,
			        k.id, pp.id, e.id, emp.id, a.lote,
			        a.username, a.rol, a.ip, a.host, a.fechaHora
			    )
			    FROM ArticuloKardex a
			    JOIN a.kardex k
			    JOIN a.presentacionProducto pp
			    JOIN a.estado e
			    JOIN a.empresa emp
			    WHERE emp.id = :empresaId
			""")
	Page<ArticuloKardexDTO> findDtoByEmpresaIdOrderByIdAsc(@Param("empresaId") Long empresaId, Pageable pageable);

	@Query("""
			    SELECT new com.coagronet.articuloKardex.dtos.ArticuloKardexDTO(
			        a.id, a.cantidad, a.precio, a.fechaVencimiento, a.identificadorProducto,
			        k.id, pp.id, est.id, emp.id, a.lote,
			        a.username, a.rol, a.ip, a.host, a.fechaHora
			    )
			    FROM ArticuloKardex a
			    JOIN a.kardex k
			    JOIN a.presentacionProducto pp
			    JOIN a.estado est
			    JOIN a.empresa emp
			    WHERE a.id = :id AND emp.id = :empresaId
			""")
	Optional<ArticuloKardexDTO> findDtoByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);

	@Query("""
			    SELECT new com.coagronet.articuloKardex.dtos.ArticuloKardexDTO(
			        a.id, a.cantidad, a.precio, a.fechaVencimiento, a.identificadorProducto,
			        k.id, pp.id, est.id, emp.id, a.lote,
			        a.username, a.rol, a.ip, a.host, a.fechaHora
			    )
			    FROM ArticuloKardex a
			    JOIN a.kardex k
			    JOIN a.presentacionProducto pp
			    JOIN a.estado est
			    JOIN a.empresa emp
			    WHERE emp.id = :empresaId AND k.id = :kardexId
			    ORDER BY a.id ASC
			""")
	List<ArticuloKardexDTO> findDtoByEmpresaIdAndKardexIdOrderByIdAsc(@Param("empresaId") Long empresaId,
			@Param("kardexId") Long kardexId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE ArticuloKardex a SET a.estado.id = 2 WHERE a.id = :id AND a.empresa.id = :empresaId")
	int softDeleteByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);

	@Query("""
        SELECT new com.coagronet.articuloKardex.dtos.KardexItemResponseDto(
            k.id, 
            p.nombre, 
            k.cantidad, 
            k.precio, 
            k.lote, 
            k.fechaVencimiento, 
            e.nombre
        )
        FROM ArticuloKardex k
        JOIN k.presentacionProducto p
        JOIN k.estado e
        WHERE k.kardex.id = :kardexId
          AND (:productoId IS NULL OR p.id = :productoId)
          AND (:estadoId IS NULL OR e.id = :estadoId)
          AND (cast(:fechaInicio as timestamp) IS NULL OR k.fechaHora >= :fechaInicio)
          AND (cast(:fechaFin as timestamp) IS NULL OR k.fechaHora <= :fechaFin)
    """)
    Page<KardexItemResponseDto> findItemsByKardexIdWithFilters(
            @Param("kardexId") Long kardexId,
            @Param("productoId") Long productoId,
            @Param("estadoId") Long estadoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

}