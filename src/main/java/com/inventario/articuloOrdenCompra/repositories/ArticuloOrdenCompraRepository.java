package com.inventario.articuloOrdenCompra.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.articuloOrdenCompra.ArticuloOrdenCompra;

public interface ArticuloOrdenCompraRepository extends JpaRepository<ArticuloOrdenCompra, Long> {

	Optional<ArticuloOrdenCompra> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<ArticuloOrdenCompra> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<ArticuloOrdenCompra> findByEmpresaIdAndOrdenCompraIdOrderByIdAsc(Long empresaId, Long ordenCompraId);

}
