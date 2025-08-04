package com.coagronet.inventario.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.inventario.dtos.InventarioDTO;
import com.coagronet.inventario.services.InventarioService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

	private final InventarioService inventarioService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<InventarioDTO>> findAll() {
		List<InventarioDTO> inventarioDTOList = inventarioService.findAll();

		return inventarioDTOList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(inventarioDTOList);
	}

	@GetMapping(params = { "inicio", "fin" })
	public ResponseEntity<List<InventarioDTO>> findByDateBetween(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		return ResponseEntity.ok(inventarioService.findByDateBetween(inicio, fin));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<InventarioDTO> findById(@PathVariable Long requestedId) {
		return inventarioService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<InventarioDTO> createInventario(@RequestBody @Valid InventarioDTO newInventario,
			UriComponentsBuilder ucb) {

		InventarioDTO savedInventarioDTO = inventarioService.create(newInventario);

		URI locationOfNewInventario = uriBuilderUtil.buildInventarioUri(savedInventarioDTO.getId(), ucb);

		return ResponseEntity.created(locationOfNewInventario).build();

	}

	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putInventario(@PathVariable Long requestedId,
			@RequestBody @Valid InventarioDTO inventarioDTOUpdate) {
		inventarioService.update(requestedId, inventarioDTOUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deleteInventario(@PathVariable Long id) {

		inventarioService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
