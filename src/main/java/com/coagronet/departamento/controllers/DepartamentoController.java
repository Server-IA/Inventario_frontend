/*=============================================================================
 Nombre del archivo : DepartamentoController.java
 Descripcion        : Controlador REST para la administracion de departamentos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.       |
 | 2026-05-29 | 1.1.0   | JUAN DIAZ            | Ajustes aplicados por PR.   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
package com.coagronet.departamento.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.departamento.dtos.DepartamentoDTO;
import com.coagronet.departamento.services.DepartamentoService;
import com.coagronet.utils.UriBuilderUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/departamento")
@RequiredArgsConstructor
@Tag(name = "Departamentos", description = "API para la administracion global de departamentos")
public class DepartamentoController {

	private final DepartamentoService departamentoService;

	private final UriBuilderUtil uriBuilderUtil;

	@Operation(summary = "Listar departamentos", description = "Obtiene departamentos globales aplicando filtros opcionales a nivel de base de datos.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Departamentos obtenidos exitosamente"),
			@ApiResponse(responseCode = "204", description = "No hay departamentos para los filtros enviados"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@GetMapping
	public ResponseEntity<List<DepartamentoDTO>> findAll(@RequestParam(required = false) Long paisId,
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) Integer codigo,
			@RequestParam(required = false) String acronimo,
			@RequestParam(required = false) Long estadoId) {
		List<DepartamentoDTO> page = departamentoService.findAll(paisId, nombre, codigo, acronimo, estadoId);

		if (page.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(page);
	}

	@Operation(summary = "Obtener departamento por ID", description = "Consulta un departamento por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Departamento encontrado"),
			@ApiResponse(responseCode = "404", description = "Departamento no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@GetMapping("/{requestedId}")
	public ResponseEntity<DepartamentoDTO> findById(@PathVariable Long requestedId) {
		return departamentoService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Crear departamento", description = "Crea un departamento dentro de un pais activo, validando unicidad por pais.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Departamento creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos invalidos, duplicados o pais inactivo"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@PostMapping
	public ResponseEntity<Void> createDepartamento(@Valid @RequestBody DepartamentoDTO departamentoDTO,
			UriComponentsBuilder ucb) {
		DepartamentoDTO savedDepartamento = departamentoService.create(departamentoDTO);
		URI locationOfNewDepartamento = uriBuilderUtil.buildDepartamentoUri(savedDepartamento.getId(), ucb);
		return ResponseEntity.created(locationOfNewDepartamento).build();
	}

	@Operation(summary = "Actualizar departamento", description = "Actualiza un departamento. Si se inactiva, tambien inactiva sus municipios.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Departamento actualizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos invalidos, duplicados o jerarquia inactiva"),
			@ApiResponse(responseCode = "404", description = "Departamento no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateDepartamento(@PathVariable Long requestedId,
			@Valid @RequestBody DepartamentoDTO departamentoDTO) {
		departamentoService.update(requestedId, departamentoDTO);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Inactivar departamento", description = "Inactiva logicamente un departamento y en cascada sus municipios.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Departamento inactivado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Departamento no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartamento(@PathVariable Long id) {
		departamentoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
