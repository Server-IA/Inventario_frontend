package com.coagronet.productoCategoria.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.productoCategoria.ProductoCategoria;

@Repository
public interface ProductoCategoriaRepository extends JpaRepository<ProductoCategoria, Long> {

	Optional<ProductoCategoria> findByIdAndEmpresaId(Long id, Long empresaId);

	List<ProductoCategoria> findByEmpresaIdOrderByIdAsc(Long empresaId);

}