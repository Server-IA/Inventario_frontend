/*=============================================================================
 Nombre del archivo : MunicipioController.java
 Descripcion        : Controlador REST para la administracion de municipios.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 | 2026-05-29 | 1.2.0   | JUAN DIAZ            | Correcciones de cierre de PR: mejoras en filtros y consultas, ajustes en controladores y servicios, y migracion SQL de localizacion global. |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.municipio.controllers;

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

import com.coagronet.municipio.dtos.MunicipioDTO;
import com.coagronet.municipio.services.MunicipioService;
import com.coagronet.utils.UriBuilderUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/municipio")
@RequiredArgsConstructor
@Tag(name = "Municipios", description = "API para la administracion global de municipios")
public class MunicipioController {

	private final MunicipioService municipioService;

	private final UriBuilderUtil uriBuilderUtil;

	@Operation(summary = "Listar municipios por departamento", description = "Obtiene municipios de un departamento aplicando filtros opcionales a nivel de base de datos.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Municipios obtenidos exitosamente"),
			@ApiResponse(responseCode = "400", description = "Departamento requerido o invalido"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@GetMapping
	public ResponseEntity<List<MunicipioDTO>> findAll(@RequestParam Long departamentoId,
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) Integer codigo,
			@RequestParam(required = false) String acronimo,
			@RequestParam(required = false) Long estadoId) {
		return ResponseEntity.ok(municipioService.findAll(departamentoId, nombre, codigo, acronimo, estadoId));
	}

	@Operation(summary = "Obtener municipio por ID", description = "Consulta un municipio por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Municipio encontrado"),
			@ApiResponse(responseCode = "404", description = "Municipio no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@GetMapping("/{requestedId}")
	public ResponseEntity<MunicipioDTO> findById(@PathVariable Long requestedId) {
		return municipioService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Crear municipio", description = "Crea un municipio dentro de un departamento activo, validando unicidad por departamento.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Municipio creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos invalidos, duplicados o jerarquia inactiva"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@PostMapping
	public ResponseEntity<Void> createMunicipio(@Valid @RequestBody MunicipioDTO municipioDTO,
			UriComponentsBuilder ucb) {
		MunicipioDTO savedMunicipio = municipioService.create(municipioDTO);
		URI locationOfNewMunicipio = uriBuilderUtil.buildMunicipioUri(savedMunicipio.getId(), ucb);
		return ResponseEntity.created(locationOfNewMunicipio).build();
	}

	@Operation(summary = "Actualizar municipio", description = "Actualiza un municipio validando que su departamento y pais permitan activacion.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Municipio actualizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos invalidos, duplicados o jerarquia inactiva"),
			@ApiResponse(responseCode = "404", description = "Municipio no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateMunicipio(@PathVariable Long requestedId,
			@Valid @RequestBody MunicipioDTO municipioDTO) {
		municipioService.update(requestedId, municipioDTO);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Inactivar municipio", description = "Inactiva logicamente un municipio.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Municipio inactivado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Municipio no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMunicipio(@PathVariable Long id) {
		municipioService.delete(id);
		return ResponseEntity.noContent().build();
	}

}









