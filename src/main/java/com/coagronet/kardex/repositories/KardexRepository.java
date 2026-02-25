package com.coagronet.kardex.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;

public interface KardexRepository extends JpaRepository<Kardex, Long> {

	@Query("""
			    SELECT new com.coagronet.kardex.dtos.KardexDTO(
			        k.id, k.fechaHora, a.id, p.id, tm.id,
			        k.descripcion, ped.id, oc.id, est.id,
			        emp.id, cp.id
			    )
			    FROM Kardex k
			    JOIN k.almacen a
			    JOIN k.produccion p
			    JOIN k.tipoMovimiento tm
			    JOIN k.estado est
			    JOIN k.empresa emp
			    LEFT JOIN k.pedido ped
			    LEFT JOIN k.ordenCompra oc
			    LEFT JOIN k.clienteProveedor cp
			    WHERE emp.id = :empresaId
			""")
	Page<KardexDTO> findDtoByEmpresaIdOrderByIdAsc(@Param("empresaId") Long empresaId, Pageable pageable);

	@Query("""
			    SELECT new com.coagronet.kardex.dtos.KardexDTO(
			        k.id, k.fechaHora, a.id, p.id, tm.id,
			        k.descripcion, ped.id, oc.id, est.id,
			        emp.id, cp.id
			    )
			    FROM Kardex k
			    JOIN k.almacen a
			    JOIN k.produccion p
			    JOIN k.tipoMovimiento tm
			    JOIN k.estado est
			    JOIN k.empresa emp
			    LEFT JOIN k.pedido ped
			    LEFT JOIN k.ordenCompra oc
			    LEFT JOIN k.clienteProveedor cp
			    WHERE k.id = :id AND emp.id = :empresaId
			""")
	Optional<KardexDTO> findDtoByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Kardex k SET k.estado.id = 2 WHERE k.id = :id AND k.empresa.id = :empresaId")
	int inactivarByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);

	Optional<Kardex> findByIdAndEmpresaId(Long id, Long empresaId);

	Optional<Kardex> findByOrdenCompraIdAndEmpresaId(Long ordenCompraId, Long empresaId);

	boolean existsByIdAndEmpresaId(Long id, Long empresaId);

	boolean existsByOrdenCompraIdAndEmpresaId(Long ordenCompraId, Long empresaId);

	@Query("SELECT k.estado.id FROM Kardex k WHERE k.id = :id AND k.empresa.id = :empresaId")
	Optional<Long> findEstadoIdByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);

	Kardex getReferenceByIdAndEmpresaId(Long id, Long empresaId);

}
