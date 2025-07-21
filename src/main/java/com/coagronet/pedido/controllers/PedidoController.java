package com.coagronet.pedido.controllers;

import java.net.URI;
import java.util.List;

import com.coagronet.articuloPedido.dtos.ArticuloPedidoDTO;
import com.coagronet.articuloPedido.services.ArticuloPedidoService;
import com.coagronet.pedido.services.PedidoService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import com.coagronet.pedido.dtos.PedidoDTO;

@RestController
@RequestMapping("/api/v1/pedido")
@RequiredArgsConstructor
public class PedidoController {

	private final PedidoService pedidoService;
	private final ArticuloPedidoService articuloPedidoService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<PedidoDTO>> findAll() {
		List<PedidoDTO> pedidoDTOList = pedidoService.findAll();

		return pedidoDTOList.isEmpty()
				? ResponseEntity.noContent().build()
				: ResponseEntity.ok(pedidoDTOList);
	}

	@GetMapping("/{pedidoId}/articulos")
	public ResponseEntity<List<ArticuloPedidoDTO>> findArticulosByPedido(
			@PathVariable Long pedidoId) {
		return ResponseEntity.ok(articuloPedidoService.findAllByPedidoId(pedidoId));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<PedidoDTO> findById(@PathVariable Long requestedId) {
		return pedidoService.findById(requestedId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createPedido(@RequestBody @Valid PedidoDTO pedidoDTO, UriComponentsBuilder ucb) {
		PedidoDTO savedPedidoDTO = pedidoService.create(pedidoDTO);

		URI locationOfNewPedido = uriBuilderUtil.buildPedidoUri(savedPedidoDTO.getId(), ucb);

		return ResponseEntity.created(locationOfNewPedido).build();
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
