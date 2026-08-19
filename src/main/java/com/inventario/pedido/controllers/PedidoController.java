package com.inventario.pedido.controllers;

import java.util.List;

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

import com.inventario.articuloPedido.dtos.ArticuloPedidoDTO;
import com.inventario.articuloPedido.services.ArticuloPedidoService;
import com.inventario.pedido.dtos.PedidoDTO;
import com.inventario.pedido.services.PedidoService;
import com.inventario.pedidocotizacion.dtos.PedidoCotizacionResponseDTO;
import com.inventario.pedidocotizacion.services.PedidoCotizacionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedido")
@RequiredArgsConstructor
public class PedidoController {

	private final PedidoService pedidoService;

	private final ArticuloPedidoService articuloPedidoService;

	private final PedidoCotizacionService pedidoCotizacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<PedidoDTO>> findAll(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(pedidoService.findAll(pageable));
	}

	@GetMapping("/{pedidoId}/articulos")
	public ResponseEntity<List<ArticuloPedidoDTO>> findArticulosByPedidoId(@PathVariable Long pedidoId) {
		return ResponseEntity.ok(articuloPedidoService.findAllByPedidoId(pedidoId));
	}

	/**
	 * Lista todas las cotizaciones asociadas a un pedido específico.
	 *
	 * @param pedidoId identificador del pedido
	 * @return lista de DTOs de respuesta
	 */
	@GetMapping("/{pedidoId}/cotizaciones")
	public ResponseEntity<List<PedidoCotizacionResponseDTO>> findAllByPedidoId(@PathVariable Long pedidoId) {
		return ResponseEntity.ok(pedidoCotizacionService.findAllByPedidoId(pedidoId));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<PedidoDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(pedidoService.findById(requestedId));
	}

	@PostMapping
	public ResponseEntity<Void> createPedido(@RequestBody @Valid PedidoDTO pedidoDTO, UriComponentsBuilder ucb) {
		return ResponseEntity.created(uriBuilderUtil.buildPedidoUri(pedidoService.create(pedidoDTO), ucb)).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updatePedido(@PathVariable Long requestedId, @Valid @RequestBody PedidoDTO pedidoDTO) {
		pedidoService.update(requestedId, pedidoDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
		pedidoService.delete(id);
		return ResponseEntity.noContent().build();
	}

	/* ===== Transiciones de estado ===== */

	// ACT -> PCO
	@PutMapping("/{id}/requiere-cotizacion")
	public ResponseEntity<Void> requiereCotizacion(@PathVariable Long id) {
		pedidoService.marcarRequiereCotizacion(id);
		return ResponseEntity.noContent().build();
	}

	// ACT/PCO/OC -> ANU
	@PutMapping("/{id}/anular")
	public ResponseEntity<Void> anular(@PathVariable Long id) {
		pedidoService.anular(id);
		return ResponseEntity.noContent().build();
	}

	// OC -> CMP (valida requisitos reales v?a InventarioGateway)
	@PutMapping("/{id}/completar")
	public ResponseEntity<Void> completar(@PathVariable Long id) {
		pedidoService.completar(id);
		return ResponseEntity.noContent().build();
	}

}
