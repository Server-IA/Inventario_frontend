package com.coagronet.ordenCompra.controllers;

import java.net.URI;
import java.util.List;

import com.coagronet.ordenCompra.services.OrdenCompraService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;


@RestController
@RequestMapping("/api/v1/orden_compra")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrdenCompraController {

	private final OrdenCompraService ordenCompraService;
	private final UriBuilderUtil uriBuilderUtil;


	@GetMapping
	public ResponseEntity<List<OrdenCompraDTO>> findAll () {
		List<OrdenCompraDTO> ordenCompraDTOList = ordenCompraService.findAll();

		return ordenCompraDTOList.isEmpty()?
				ResponseEntity.noContent().build()
				: ResponseEntity.ok(ordenCompraDTOList);

	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<OrdenCompraDTO> findById (@PathVariable Long requestedId) {
		return ordenCompraService.findById(requestedId).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());

	}


	@PostMapping
	public ResponseEntity<Void> crearOrdenCompra(@RequestBody @Valid OrdenCompraDTO ordenCompraDTODTO, UriComponentsBuilder ucb) {
		OrdenCompraDTO savedOrdenCompraDTO = ordenCompraService.create(ordenCompraDTODTO);

		URI locationOfNewOrdenCompra = uriBuilderUtil.buildOrdenCompraUri(savedOrdenCompraDTO.getId(), ucb);
		return ResponseEntity.created(locationOfNewOrdenCompra).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> actualizarOrdenCompra(@PathVariable Long requestedId,
													 @RequestBody OrdenCompraDTO ordenCompraDTODTO) {

		ordenCompraService.update(requestedId, ordenCompraDTODTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{requestedId}")
	public ResponseEntity<Void> eliminarOrdenCompra(@PathVariable Long requestedId) {
		ordenCompraService.delete(requestedId);
		return ResponseEntity.noContent().build();
	}
}
