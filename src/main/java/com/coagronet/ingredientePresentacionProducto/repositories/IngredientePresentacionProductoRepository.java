package com.coagronet.ingredientePresentacionProducto.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.ingredientePresentacionProducto.IngredientePresentacionProducto;

@Repository
public interface IngredientePresentacionProductoRepository
		extends JpaRepository<IngredientePresentacionProducto, Long> {

	Optional<IngredientePresentacionProducto> findByIdAndEmpresaId(Long id, Long empresaId);

	List<IngredientePresentacionProducto> findByEmpresaIdOrderByIdAsc(Long empresaId);

	@EntityGraph(attributePaths = {
			"ingrediente",
			"presentacionProducto",
			"presentacionProducto.producto",
			"unidad",
			"estado"
	})
	Page<IngredientePresentacionProducto> findByEmpresaId(Long empresaId, Pageable pageable);

}
