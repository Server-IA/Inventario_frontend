package com.inventario.producto.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.inventario.producto.dtos.ProductoRequestDTO;
import com.inventario.producto.dtos.ProductoResponseDTO;
import com.inventario.producto.services.ProductoService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/productos")
@RequiredArgsConstructor
public class ProductoController {

	private final ProductoService productoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<ProductoResponseDTO>> findAllByFilters(
			@PageableDefault Pageable pageable,
			@org.springframework.web.bind.annotation.RequestParam(required = false) Long estado,
			@org.springframework.web.bind.annotation.RequestParam(required = false) Long categoria) {

		return ResponseEntity.ok(productoService.findAllByFilters(pageable, estado, categoria));
	}

	@GetMapping("/{requestedId}")
	private ResponseEntity<ProductoResponseDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(productoService.findById(requestedId));
	}

	@PostMapping
	private ResponseEntity<Void> createProducto(@Valid @RequestBody ProductoRequestDTO newProductoRequest,
			UriComponentsBuilder ucb) {
		return ResponseEntity
				.created(uriBuilderUtil
						.buildProductoUri((productoService.create(newProductoRequest)).id(),
								ucb))
				.build();
	}

	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putProducto(@PathVariable Long requestedId,
			@RequestBody @Valid ProductoRequestDTO productoDTOUpdate) {
		productoService.update(requestedId, productoDTOUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
		productoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
