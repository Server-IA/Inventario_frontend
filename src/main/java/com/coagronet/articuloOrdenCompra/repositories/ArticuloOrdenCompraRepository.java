package com.coagronet.articuloOrdenCompra.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.articuloOrdenCompra.ArticuloOrdenCompra;

public interface ArticuloOrdenCompraRepository extends JpaRepository<ArticuloOrdenCompra, Long> {

    Optional<ArticuloOrdenCompra> findByIdAndEmpresaId(Long id, Long empresaId);

    List<ArticuloOrdenCompra> findByEmpresaIdOrderByIdAsc(Long empresaId);

    List<ArticuloOrdenCompra> findByEmpresaIdAndOrdenCompraIdOrderByIdAsc(Long empresaId, Long ordenCompraId);

}
