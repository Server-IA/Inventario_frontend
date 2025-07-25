package com.coagronet.proveedor.controllers;

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

import com.coagronet.proveedor.dtos.ProveedorDTO;
import com.coagronet.proveedor.services.ProveedorService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proveedor")
@RequiredArgsConstructor
public class ProveedorController {

	private final ProveedorService proveedorService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<ProveedorDTO>> findAll() {
		List<ProveedorDTO> proveedorDTOList = proveedorService.findAll();

		return proveedorDTOList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(proveedorDTOList);

	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ProveedorDTO> findById(@PathVariable Long requestedId) {
		return proveedorService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

	}

	@PostMapping
	public ResponseEntity<Void> crearProveedor(@RequestBody @Valid ProveedorDTO proveedorDTO,
			UriComponentsBuilder ucb) {
		ProveedorDTO savedProveedorDTO = proveedorService.create(proveedorDTO);

		URI locationOfNewProveedor = uriBuilderUtil.buildProveedorUri(savedProveedorDTO.getId(), ucb);
		return ResponseEntity.created(locationOfNewProveedor).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> actualizarProveedor(@PathVariable Long requestedId,
			@RequestBody ProveedorDTO proveedorDTO) {

		proveedorService.update(requestedId, proveedorDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{requestedId}")
	public ResponseEntity<Void> eliminarProveedor(@PathVariable Long requestedId) {
		proveedorService.delete(requestedId);
		return ResponseEntity.noContent().build();
	}

}
