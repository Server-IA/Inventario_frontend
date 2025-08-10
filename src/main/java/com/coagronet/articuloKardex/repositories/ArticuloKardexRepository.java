package com.coagronet.articuloKardex.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.articuloKardex.ArticuloKardex;

@Repository
public interface ArticuloKardexRepository extends JpaRepository<ArticuloKardex, Long> {

	Optional<ArticuloKardex> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<ArticuloKardex> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<ArticuloKardex> findByEmpresaIdAndKardexIdOrderByIdAsc(Long empresaId, Long kardexId);

	Optional<ArticuloKardex> findByidentificadorProductoAndEmpresaId(String identificadorProducto, Long empresaId);

}