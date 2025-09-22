package com.coagronet.articuloPedido.controllers;

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
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/articulo-pedido")
@RequiredArgsConstructor
public class ArticuloPedidoController {

	private final ArticuloPedidoService articuloPedidoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<ArticuloPedidoDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<ArticuloPedidoDTO> page = articuloPedidoService.findAll(pageable);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ArticuloPedidoDTO> findById(@PathVariable Long requestedId) {
		return articuloPedidoService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createArticuloPedido(@Valid @RequestBody ArticuloPedidoDTO articuloPedidoDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil.buildArticuloPedidoUri((articuloPedidoService.create(articuloPedidoDTO)).getId(),
					ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateArticuloPedido(@PathVariable Long requestedId,
			@Valid @RequestBody ArticuloPedidoDTO articuloPedidoDTO) {
		articuloPedidoService.update(requestedId, articuloPedidoDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteArticuloPedido(@PathVariable Long id) {
		articuloPedidoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}