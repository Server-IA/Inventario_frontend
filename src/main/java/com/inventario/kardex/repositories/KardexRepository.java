package com.inventario.kardex.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventario.kardex.Kardex;

public interface KardexRepository extends JpaRepository<Kardex, Long>, JpaSpecificationExecutor<Kardex> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Kardex k SET k.estado.id = :estadoInactivoId WHERE k.id = :kardexId AND k.estado.id = :estadoActivoId AND k.empresa.id = :empresaId")
	int inactivarKardex(@Param("kardexId") Long kardexId, @Param("empresaId") Long empresaId,
			@Param("estadoActivoId") Long estadoActivoId, @Param("estadoInactivoId") Long estadoInactivoId);

	@EntityGraph(attributePaths = { "items" })
	Optional<Kardex> findWithItemsById(Long id);

}
