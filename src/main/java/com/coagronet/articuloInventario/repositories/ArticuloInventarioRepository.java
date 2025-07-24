package com.coagronet.articuloInventario.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.articuloInventario.ArticuloInventario;

@Repository
public interface ArticuloInventarioRepository extends JpaRepository<ArticuloInventario, Long> {

	Optional<ArticuloInventario> findByIdAndEmpresaId(Long id, Long empresaId);

	List<ArticuloInventario> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
