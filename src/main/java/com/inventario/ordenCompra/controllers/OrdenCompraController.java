package com.inventario.ordenCompra.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.inventario.articuloOrdenCompra.dtos.ArticuloOrdenCompraDTO;
import com.inventario.articuloOrdenCompra.services.ArticuloOrdenCompraService;
import com.inventario.ordenCompra.dtos.OrdenCompraCreateDTO;
import com.inventario.ordenCompra.dtos.OrdenCompraDTO;
import com.inventario.ordenCompra.dtos.OrdenCompraLookupDTO;
import com.inventario.ordenCompra.services.OrdenCompraService;
import com.inventario.utils.UriBuilderUtil;
import com.inventario.utils.UserEmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orden-compra")
@RequiredArgsConstructor
public class OrdenCompraController {

	private final OrdenCompraService ordenCompraService;

	private final ArticuloOrdenCompraService articuloOrdenCompraService;

	private final UriBuilderUtil uriBuilderUtil;

	private final UserEmpresaService userEmpresaService;

	@GetMapping
	public ResponseEntity<Page<OrdenCompraDTO>> findAll(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(ordenCompraService.findAll(pageable));
	}

	@GetMapping("/{ordenCompraId}/articulos")
	public ResponseEntity<List<ArticuloOrdenCompraDTO>> findArticulosByOrdenCompra(@PathVariable Long ordenCompraId) {
		return ResponseEntity.ok(articuloOrdenCompraService.findAllByOrdenCompraId(ordenCompraId));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<OrdenCompraDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(ordenCompraService.findById(requestedId));
	}

	@PostMapping
	public ResponseEntity<Void> createOrdenCompra(@Valid @RequestBody OrdenCompraCreateDTO ordenCompraCreateDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil.buildOrdenCompraUri((ordenCompraService.create(ordenCompraCreateDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateOrdenCompra(@PathVariable Long requestedId,
			@Valid @RequestBody OrdenCompraDTO ordenCompraDTO) {
		ordenCompraService.update(requestedId, ordenCompraDTO);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/enviar-al-proveedor/{ordenCompraId}")
	public ResponseEntity<Void> enviarOrdenCompraAlProveedor(@PathVariable Long ordenCompraId) {
		ordenCompraService.enviarOrdenCompraAlProveedor(ordenCompraId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrdenCompra(@PathVariable Long id) {
		ordenCompraService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/pedido/{pedidoId}/lookup")
	public ResponseEntity<List<OrdenCompraLookupDTO>> getLookup(@PathVariable Long pedidoId) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return ResponseEntity.ok(ordenCompraService.listarParaSeleccion(pedidoId, empresaId));
	}

}
