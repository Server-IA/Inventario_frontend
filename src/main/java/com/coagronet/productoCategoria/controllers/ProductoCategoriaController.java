package com.coagronet.productoCategoria.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.productoCategoria.dtos.ProductoCategoriaDTO;
import com.coagronet.productoCategoria.services.ProductoCategoriaService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/producto_categoria")
@RequiredArgsConstructor
public class ProductoCategoriaController {

	private final ProductoCategoriaService productoCategoriaService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<ProductoCategoriaDTO>> findAll() {
		return ResponseEntity.ok(productoCategoriaService.findAll());
	}

	@PostMapping
	public ResponseEntity<Void> createProductoCategoria(@Valid @RequestBody ProductoCategoriaDTO productoCategoriaDTO,
			UriComponentsBuilder ucb) {
		ProductoCategoriaDTO savedProductoCategoria = productoCategoriaService.create(productoCategoriaDTO);
		URI locationOfNewProductoCategoria = uriBuilderUtil.buildProductoCategoriaUri(savedProductoCategoria.getId(),
				ucb);
		return ResponseEntity.created(locationOfNewProductoCategoria).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateProductoCategoria(@PathVariable Long requestedId,
			@Valid @RequestBody ProductoCategoriaDTO productoCategoriaDTO) {
		productoCategoriaService.update(requestedId, productoCategoriaDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProductoCategoria(@PathVariable Long id) {
		productoCategoriaService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
