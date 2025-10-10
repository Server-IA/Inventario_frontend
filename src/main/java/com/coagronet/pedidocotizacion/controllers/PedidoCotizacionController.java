package com.coagronet.pedidocotizacion.controllers;

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

import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionRequestDTO;
import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionResponseDTO;
import com.coagronet.pedidocotizacion.services.PedidoCotizacionService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedido-cotizacion")
@RequiredArgsConstructor
public class PedidoCotizacionController {

	private final PedidoCotizacionService pedidoCotizacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<PedidoCotizacionResponseDTO>> findAll(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(pedidoCotizacionService.findAll(pageable));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<PedidoCotizacionResponseDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(pedidoCotizacionService.findById(requestedId));
	}

	@PostMapping
	public ResponseEntity<Void> createPedidoCotizacion(
			@Valid @RequestBody PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO, UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil
				.buildOrdenCompraUri((pedidoCotizacionService.create(pedidoCotizacionRequestDTO)).id(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updatePedidoCotizacion(@PathVariable Long requestedId,
			@Valid @RequestBody PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO) {
		pedidoCotizacionService.update(requestedId, pedidoCotizacionRequestDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePedidoCotizacion(@PathVariable Long id) {
		pedidoCotizacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
