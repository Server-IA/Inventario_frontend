package com.inventario.estado.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventario.estado.Estado;

public interface EstadoRepository extends JpaRepository<Estado, Long> {

	Optional<Estado> findByIdAndEstadoCategoriaId(Long id, Long estadoCategoriaId);

	@Query("""
			    select e.id from Estado e
			    where e.estadoCategoria.id = :catId
			      and e.acronimo = :acr
			""")
	Optional<Long> findIdByCategoriaIdAndAcronimo(@Param("catId") Long categoriaId, @Param("acr") String acronimo);

}
