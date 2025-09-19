package com.coagronet.pedido.controllers;

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

import com.coagronet.articuloPedido.dtos.ArticuloPedidoDTO;
import com.coagronet.articuloPedido.services.ArticuloPedidoService;
import com.coagronet.pedido.dtos.PedidoDTO;
import com.coagronet.pedido.services.PedidoService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedido")
@RequiredArgsConstructor
public class PedidoController {

	private final PedidoService pedidoService;

	private final ArticuloPedidoService articuloPedidoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<PedidoDTO>> findAll(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(pedidoService.findAll(pageable));
	}

	@GetMapping("/{pedidoId}/articulos")
	public ResponseEntity<List<ArticuloPedidoDTO>> findArticulosByPedidoId(@PathVariable Long pedidoId) {
		return ResponseEntity.ok(articuloPedidoService.findAllByPedidoId(pedidoId));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<PedidoDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(pedidoService.findById(requestedId));
	}

	@PostMapping
	public ResponseEntity<Void> createPedido(@RequestBody @Valid PedidoDTO pedidoDTO, UriComponentsBuilder ucb) {
		return ResponseEntity.created(uriBuilderUtil.buildPedidoUri(pedidoService.create(pedidoDTO).getId(), ucb))
			.build();
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

}
