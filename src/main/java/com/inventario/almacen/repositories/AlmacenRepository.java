package com.inventario.almacen.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventario.almacen.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

	Optional<Almacen> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Almacen> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<Almacen> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

	@Query("SELECT a FROM Almacen a WHERE a.id = :id AND a.estado.id = :estadoId")
	Optional<Almacen> findByIdAndEstadoId(@Param("id") Long id, @Param("estadoId") Long estadoId);

}
