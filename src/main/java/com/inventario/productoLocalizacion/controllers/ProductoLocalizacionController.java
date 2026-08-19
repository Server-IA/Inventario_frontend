package com.inventario.productoLocalizacion.controllers;

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

import com.inventario.productoLocalizacion.dtos.ProductoLocalizacionDTO;
import com.inventario.productoLocalizacion.services.ProductoLocalizacionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/producto_localizacion")
@RequiredArgsConstructor
public class ProductoLocalizacionController {

	private final ProductoLocalizacionService productoLocalizacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<ProductoLocalizacionDTO>> findAll() {
		List<ProductoLocalizacionDTO> productoLocalizacionDTOList = productoLocalizacionService.findAll();

		return productoLocalizacionDTOList.isEmpty() ? ResponseEntity.noContent().build()
				: ResponseEntity.ok(productoLocalizacionDTOList);

	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ProductoLocalizacionDTO> findById(@PathVariable Long requestedId) {
		return productoLocalizacionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());

	}

	@PostMapping
	public ResponseEntity<Void> crearProductoLocalizacion(
			@RequestBody @Valid ProductoLocalizacionDTO productoLocalizacionDTO, UriComponentsBuilder ucb) {
		ProductoLocalizacionDTO savedProductoLocalizacionDTO = productoLocalizacionService
			.create(productoLocalizacionDTO);

		URI locationOfNewProductoLocalizacion = uriBuilderUtil
			.buildProductoLocalizacionUri(savedProductoLocalizacionDTO.getId(), ucb);
		return ResponseEntity.created(locationOfNewProductoLocalizacion).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> actualizarProductoLocalizacion(@PathVariable Long requestedId,
			@RequestBody ProductoLocalizacionDTO productoLocalizacionDTO) {

		productoLocalizacionService.update(requestedId, productoLocalizacionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{requestedId}")
	public ResponseEntity<Void> eliminarProductoLocalizacion(@PathVariable Long requestedId) {
		productoLocalizacionService.delete(requestedId);
		return ResponseEntity.noContent().build();
	}

}
