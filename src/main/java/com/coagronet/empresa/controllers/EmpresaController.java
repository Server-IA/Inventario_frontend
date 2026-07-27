/*=============================================================================
 Nombre del archivo : EmpresaController.java
 Descripcion        : Controlador REST para la gestion de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2024-08-16 | 1.0.0   | yourusername         | Creacion del archivo.                                                                                                              |
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Se implementa el endpoint de registro de empresas con soporte JSON y multipart para la HU-043.1.                                  |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.dtos.EmpresaDTO;
import com.coagronet.empresa.dtos.EmpresaRegistroRequestDTO;
import com.coagronet.empresa.dtos.EmpresaRegistroResponseDTO;
import com.coagronet.empresa.mappers.EmpresaMapper;
import com.coagronet.empresa.services.EmpresaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {

	private final EmpresaService empresaService;

	@GetMapping
	public ResponseEntity<?> getAllEmpresas(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sortBy) {

		String[] sortParams = sortBy.split(",");
		if (sortParams.length != 2
				|| (!sortParams[1].equalsIgnoreCase("asc") && !sortParams[1].equalsIgnoreCase("desc"))) {
			return ResponseEntity.badRequest()
				.body("El parámetro 'sortBy' debe tener el formato 'campo,dirección', donde dirección es 'asc' o 'desc'.");
		}

		Sort sort = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

		Pageable pageable = PageRequest.of(page, size, sort);
		Page<EmpresaDTO> empresaPage = empresaService.getAllEmpresas(pageable)
			.map(EmpresaMapper.INSTANCE::toEmpresaDTO);

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> header = new HashMap<>();
		header.put("totalElements", empresaPage.getTotalElements());
		header.put("totalPages", empresaPage.getTotalPages());
		header.put("size", empresaPage.getSize());
		header.put("number", empresaPage.getNumber());
		header.put("sort", empresaPage.getSort());
		header.put("first", empresaPage.isFirst());
		header.put("last", empresaPage.isLast());
		header.put("numberOfElements", empresaPage.getNumberOfElements());
		header.put("empty", empresaPage.isEmpty());

		response.put("header", header);
		response.put("data", empresaPage.getContent());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmpresaDTO> getEmpresaById(@PathVariable Long id) {
		Empresa empresa = empresaService.getEmpresaById(id);
		if (empresa != null) {
			return ResponseEntity.ok(EmpresaMapper.INSTANCE.toEmpresaDTO(empresa));
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "Registrar empresa",
			description = "Registra una empresa sin logo mediante un cuerpo JSON y la asocia con una persona responsable.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Empresa registrada exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos obligatorios o relaciones invalidas"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Usuario sin permiso de gestion de empresas"),
			@ApiResponse(responseCode = "409", description = "Identificacion o correo duplicado")
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmpresaRegistroResponseDTO> registrarEmpresa(
			@Valid @RequestBody EmpresaRegistroRequestDTO request) {
		return respuestaCreada(empresaService.registrar(request, null));
	}

	@Operation(summary = "Registrar empresa con logo",
			description = "Registra una empresa y procesa un logo PNG opcional enviado como multipart/form-data.")
	@ApiResponses({
			@ApiResponse(responseCode = "201",
					description = "Empresa registrada; la respuesta indica si el logo fue cargado o rechazado"),
			@ApiResponse(responseCode = "400", description = "Datos obligatorios o relaciones invalidas"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Usuario sin permiso de gestion de empresas"),
			@ApiResponse(responseCode = "409", description = "Identificacion o correo duplicado")
	})
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmpresaRegistroResponseDTO> registrarEmpresaConLogo(
			@Valid @RequestPart("empresa") EmpresaRegistroRequestDTO request,
			@RequestPart(value = "logo", required = false) MultipartFile logo) {
		return respuestaCreada(empresaService.registrar(request, logo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EmpresaDTO> updateEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
		Empresa empresa = EmpresaMapper.INSTANCE.toEmpresa(empresaDTO);
		empresa.setId(id);
		Empresa updatedEmpresa = empresaService.update(empresa);
		return ResponseEntity.ok(EmpresaMapper.INSTANCE.toEmpresaDTO(updatedEmpresa));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmpresa(@PathVariable Long id) {
		empresaService.deleteEmpresa(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/logo")
	public ResponseEntity<String> subirLogoEmpresa(@RequestParam("file") MultipartFile file) {
		empresaService.subirLogoDesdeEmpresaLogueada(file);
		return ResponseEntity.ok("Logo subido exitosamente");
	}

	private ResponseEntity<EmpresaRegistroResponseDTO> respuestaCreada(EmpresaRegistroResponseDTO response) {
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(response.getId())
			.toUri();
		return ResponseEntity.created(location).body(response);
	}

}
