package com.coagronet.kardex.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.kardex.Kardex;

public interface KardexRepository extends JpaRepository<Kardex, Long>, JpaSpecificationExecutor<Kardex> {

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

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Kardex k SET k.estado.id = :estadoInactivoId WHERE k.id = :kardexId AND k.estado.id = :estadoActivoId AND k.empresa.id = :empresaId")
	int inactivarKardex(@Param("kardexId") Long kardexId, @Param("empresaId") Long empresaId,
			@Param("estadoActivoId") Long estadoActivoId, @Param("estadoInactivoId") Long estadoInactivoId);

	@EntityGraph(attributePaths = { "items" })
	Optional<Kardex> findWithItemsById(Long id);

}
