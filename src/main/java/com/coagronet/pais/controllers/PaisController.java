/*=============================================================================
 Nombre del archivo : PaisController.java
 Descripcion        : Controlador REST para la administracion de paises.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.       |
 | 2026-05-29 | 1.1.0   | JUAN DIAZ            | Ajustes aplicados por PR.   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
package com.coagronet.pais.controllers;

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

import com.coagronet.pais.dtos.PaisDTO;
import com.coagronet.pais.services.PaisService;
import com.coagronet.utils.UriBuilderUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pais")
@RequiredArgsConstructor
@Tag(name = "Paises", description = "API para la administracion global de paises")
public class PaisController {

	private final PaisService paisService;

	private final UriBuilderUtil uriBuilderUtil;

	@Operation(summary = "Listar paises", description = "Obtiene el catalogo global de paises.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Paises obtenidos exitosamente"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@GetMapping
	public ResponseEntity<List<PaisDTO>> findAll() {
		return ResponseEntity.ok(paisService.findAll());
	}

	@Operation(summary = "Obtener pais por ID", description = "Consulta un pais del catalogo global por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Pais encontrado"),
			@ApiResponse(responseCode = "404", description = "Pais no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@GetMapping("/{requestedId}")
	public ResponseEntity<PaisDTO> findById(@PathVariable Long requestedId) {
		return paisService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Crear pais", description = "Crea un pais global validando unicidad de nombre, codigo y acronimo.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Pais creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos invalidos o duplicados"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@PostMapping
	public ResponseEntity<Void> createPais(@Valid @RequestBody PaisDTO paisDTO, UriComponentsBuilder ucb) {
		PaisDTO savedPais = paisService.create(paisDTO);
		URI locationOfNewPais = uriBuilderUtil.buildPaisUri(savedPais.getId(), ucb);
		return ResponseEntity.created(locationOfNewPais).build();
	}

	@Operation(summary = "Actualizar pais", description = "Actualiza un pais global. Si se inactiva, tambien inactiva sus departamentos y municipios.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Pais actualizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos invalidos o duplicados"),
			@ApiResponse(responseCode = "404", description = "Pais no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updatePais(@PathVariable Long requestedId, @Valid @RequestBody PaisDTO paisDTO) {
		paisService.update(requestedId, paisDTO);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Inactivar pais", description = "Inactiva logicamente un pais y en cascada sus departamentos y municipios.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Pais inactivado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Pais no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePais(@PathVariable Long id) {
		paisService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
