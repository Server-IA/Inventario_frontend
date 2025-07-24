package com.coagronet.tipoSede.controllers;

import java.net.URI;
import java.util.List;

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

import com.coagronet.tipoSede.dtos.TipoSedeDTO;
import com.coagronet.tipoSede.services.TipoSedeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tipo_sede")
@RequiredArgsConstructor
public class TipoSedeController {

	private final TipoSedeService tipoSedeService;

	@GetMapping("/{requestedId}")
	public ResponseEntity<TipoSedeDTO> findById(@PathVariable Long requestedId) {
		return tipoSedeService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createTipoSede(@RequestBody TipoSedeDTO newTipoSedeRequest, UriComponentsBuilder ucb) {
		TipoSedeDTO savedTipoSede = tipoSedeService.create(newTipoSedeRequest);
		URI locationOfNewTipoSede = ucb.path("/api/v1/tipo_sede/{id}").buildAndExpand(savedTipoSede.getId()).toUri();
		return ResponseEntity.created(locationOfNewTipoSede).build();
	}

	@GetMapping
	public ResponseEntity<List<TipoSedeDTO>> findAll() {
		return ResponseEntity.ok(tipoSedeService.findAll());
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> putTipoSede(@PathVariable Long requestedId,
			@RequestBody TipoSedeDTO tipoSedeDTOUpdate) {
		tipoSedeService.update(requestedId, tipoSedeDTOUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTipoSede(@PathVariable Long id) {
		tipoSedeService.delete(id);
		return ResponseEntity.ok().build();
	}

}
