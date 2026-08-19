package com.inventario.articuloKardex.controllers;

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

import com.inventario.articuloKardex.dtos.ArticuloKardexDTO;
import com.inventario.articuloKardex.services.ArticuloKardexService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articulo-kardex")
@RequiredArgsConstructor
public class ArticuloKardexController {

	private final ArticuloKardexService articuloKardexService;

	private final UriBuilderUtil uriBuilderUtil;

	private final String version = "1.5.0";

	@GetMapping
	public ResponseEntity<Page<ArticuloKardexDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<ArticuloKardexDTO> page = articuloKardexService.findAll(pageable);
		if (page.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{kardexId}/articulos")
	public ResponseEntity<List<ArticuloKardexDTO>> findArticulosByKardex(@PathVariable Long kardexId) {
		return ResponseEntity.ok(articuloKardexService.findAllByKardexId(kardexId));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ArticuloKardexDTO> findById(@PathVariable Long requestedId) {
		return articuloKardexService.findById(requestedId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createArticuloKardex(
			@Valid @RequestBody ArticuloKardexDTO articuloKardexDTO,
			UriComponentsBuilder ucb,
			HttpServletRequest request) {
		var creado = articuloKardexService.create(articuloKardexDTO, request);

		return ResponseEntity
				.created(uriBuilderUtil.buildArticuloKardexUri(creado.getId(), ucb))
				.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateArticuloKardex(@PathVariable Long requestedId,
			@Valid @RequestBody ArticuloKardexDTO articuloKardexDTO) {
		articuloKardexService.update(requestedId, articuloKardexDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteArticuloKardex(@PathVariable Long id) {
		articuloKardexService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/version")
	public ResponseEntity<String> version() {
		return ResponseEntity.ok("Version del controlador de kardex item " + version);
	}

}
