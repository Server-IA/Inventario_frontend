package com.coagronet.facturacion.controllers;

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

import com.coagronet.facturacion.dtos.FacturacionDTO;
import com.coagronet.facturacion.services.FacturacionService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/factura")
@RequiredArgsConstructor
public class FacturacionController {

	private final FacturacionService facturacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<FacturacionDTO>> findAll(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(facturacionService.findAll(pageable));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<FacturacionDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(facturacionService.findById(requestedId));
	}

	@PostMapping
	public ResponseEntity<Void> createFacturacion(@Valid @RequestBody FacturacionDTO facturacionDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil.buildFacturacionUri((facturacionService.create(facturacionDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateFacturacion(@PathVariable Long requestedId,
			@Valid @RequestBody FacturacionDTO facturacionDTO) {
		facturacionService.update(requestedId, facturacionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFacturacion(@PathVariable Long id) {
		facturacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
