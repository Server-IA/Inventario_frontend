package com.coagronet.estadoCategoria.controllers;

import com.coagronet.estadoCategoria.EstadoCategoria;
import com.coagronet.estadoCategoria.dtos.EstadoCategoriaDTO;
import com.coagronet.estadoCategoria.mappers.EstadoCategoriaMapper;
import com.coagronet.estadoCategoria.repositories.EstadoCategoriaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/estado_categoria")
@CrossOrigin(origins = "*")
public class EstadoCategoriaController {

	private final EstadoCategoriaMapper estadoCategoriaMapper;

	private final EstadoCategoriaRepository estadoCategoriaRepository;

	@GetMapping("/{requestedId}")
	public ResponseEntity<EstadoCategoria> findById(@PathVariable Long requestedId) {
		return estadoCategoriaRepository.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<EstadoCategoria>> findAll(Pageable pageable) {
		Page<EstadoCategoria> page = estadoCategoriaRepository.findAll(pageable);
		return ResponseEntity.ok(page.getContent());
	}

	@GetMapping("/short")
	public ResponseEntity<List<EstadoCategoriaDTO>> listadoEstadoCategorias() {
		List<EstadoCategoria> estadoCategorias = estadoCategoriaRepository.findAll();
		List<EstadoCategoriaDTO> datosListadoEstadoCategorias = estadoCategorias.stream()
			.map(estadoCategoriaMapper::toDTO)
			.collect(Collectors.toList());
		return ResponseEntity.ok(datosListadoEstadoCategorias);
	}

	@PostMapping
	public ResponseEntity<Void> createEstadoCategoria(@RequestBody @Valid EstadoCategoriaDTO newEstadoCategoriaRequest,
			UriComponentsBuilder ucb) {

		EstadoCategoria savedEstadoCategoria = estadoCategoriaRepository
			.save(estadoCategoriaMapper.toEntity(newEstadoCategoriaRequest));
		URI locationOfNewEstadoCategoria = ucb.path("/api/v1/estadoCategorias/{id}")
			.buildAndExpand(savedEstadoCategoria.getId())
			.toUri();
		return ResponseEntity.created(locationOfNewEstadoCategoria).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Object> putEstadoCategoria(@PathVariable Long requestedId,
			@RequestBody EstadoCategoria estadoCategoriaUpdate) {
		return estadoCategoriaRepository.findById(requestedId).map(estadoCategoria -> {
			estadoCategoria.setNombre(estadoCategoriaUpdate.getNombre());
			estadoCategoria.setDescripcion(estadoCategoriaUpdate.getDescripcion());
			estadoCategoriaRepository.save(estadoCategoria);
			return ResponseEntity.noContent().build();
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEstadoCategoria(@PathVariable Long id) {
		if (estadoCategoriaRepository.existsById(id)) {
			estadoCategoriaRepository.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

}
