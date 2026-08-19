package com.inventario.producto.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventario.producto.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

	Optional<Producto> findByIdAndEmpresaId(Long id, Long empresaId);

	@Query("""
			SELECT p FROM Producto p
			WHERE p.empresa.id = :empresaId
			  AND (:estadoId IS NULL OR p.estado.id = :estadoId)
			  AND (:categoriaId IS NULL OR p.productoCategoria.id = :categoriaId)
			ORDER BY p.id ASC
			""")
	Page<Producto> findByFilters(Long empresaId, Long estadoId, Long categoriaId, Pageable pageable);

}
