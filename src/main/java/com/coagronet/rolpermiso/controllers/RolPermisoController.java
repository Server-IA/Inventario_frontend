package com.coagronet.rolpermiso.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.rolpermiso.dtos.request.AsignarModulosMetodosRequest;
import com.coagronet.rolpermiso.dtos.request.AsignarModulosPermisoRequest;
import com.coagronet.rolpermiso.dtos.request.AsignarPermisosRequest;
import com.coagronet.rolpermiso.dtos.request.ReemplazarModuloRequest;
import com.coagronet.rolpermiso.dtos.request.ReemplazarPermisoRequest;
import com.coagronet.rolpermiso.dtos.response.ModuloPermisoResponse;
import com.coagronet.rolpermiso.dtos.response.PermisoResponse;
import com.coagronet.rolpermiso.dtos.response.RolPermisoAsignadoResponse;
import com.coagronet.rolpermiso.services.RolPermisoService;
import com.coagronet.rolpermiso.utils.RolPermisoDualAuthResolver;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * <h5>CONTROLLER para ADMINISTRADOR_EMPRESA</h5> Gestiona la asignación de permisos a
 * roles, con soporte para seleccionar por módulos.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/empresa-rol-permisos")
public class RolPermisoController {

	private final RolPermisoService rolPermisoService;
	private final RolPermisoDualAuthResolver dualAuthResolver;

	/**
	 * GET: Obtener módulos disponibles con sus permisos agrupados (con paginación) Admin
	 * de empresa usa esto para ver qué módulos puede asignar a los roles
	 *
	 * Parámetros de paginación: - page: número de página (default: 0) - size: elementos
	 * por página (default: 20) - sort: ordenamiento por campo (ej: sort=moduloNombre,asc)
	 */
	@GetMapping("/modulos-disponibles")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Page<ModuloPermisoResponse>> getModulosDisponibles(Pageable pageable) {
		return ResponseEntity.ok(rolPermisoService.getModulosDisponibles(pageable));
	}

	/**
	 * GET: Obtener módulos por subsistema(s) con permisos agrupados (sin paginación)
	 * Parámetro: subsistemaIds=1,2,3 Usado para que admin de empresa seleccione
	 * subsistemas y luego elija permisos individuales
	 */
	@GetMapping("/modulos-subsistema")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.OK)
	public List<ModuloPermisoResponse> getModulosBySubsistemas(@RequestParam List<Long> subsistemaIds) {
		return rolPermisoService.getModulosBySubsistemas(subsistemaIds);
	}

	/**
	 * POST: Asignar TODOS los permisos de módulos seleccionados a un rol
	 *
	 * Body ejemplo (el `rolId` va en la URL como path variable): { "modulosIds": [342,
	 * 340] }
	 *
	 * Resultado: Se asignan todos los permisos donde modulo_id IN (342, 340) al rol
	 */
	@PostMapping("/{rolId}/asignar-modulos-permisos")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<RolPermisoAsignadoResponse> asignarModulosPermisos(@PathVariable Long rolId,
			@RequestBody @Valid AsignarModulosPermisoRequest dto) {

		RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisos(rolId, dto.getModulosIds());

		return ResponseEntity.created(URI.create("/api/v1/empresa-rol/" + rolId)).body(response);
	}

	/**
	 * POST: Asignar SOLO permisos de lectura (GET/READ) de los módulos seleccionados
	 *
	 * Body ejemplo (el `rolId` va en la URL como path variable): { "modulosIds": [342,
	 * 340] }
	 */
	@PostMapping("/{rolId}/asignar-modulos-lectura")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<RolPermisoAsignadoResponse> asignarModulosPermisosLectura(@PathVariable Long rolId,
			@RequestBody @Valid AsignarModulosPermisoRequest dto) {

		RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisosLectura(rolId,
				dto.getModulosIds());

		return ResponseEntity.created(URI.create("/api/v1/empresa-rol/" + rolId + "/lectura")).body(response);
	}

	/**
	 * POST: Asignar permisos de módulos seleccionados filtrando por métodos indicados por
	 * módulo. Body ejemplo (el `rolId` va en la URL como path variable): {
	 * "modulosMetodos": [ {"moduloId": 342, "metodos": ["GET","POST"]}, {"moduloId": 340,
	 * "metodos": ["ALL"]} ] }
	 */
	@PostMapping("/{rolId}/asignar-modulos-metodos")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<RolPermisoAsignadoResponse> asignarModulosPermisosConMetodos(@PathVariable Long rolId,
			@RequestBody @Valid AsignarModulosMetodosRequest dto) {

		RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisosConMetodos(rolId,
				dto.getModulosMetodos());

		return ResponseEntity.created(URI.create("/api/v1/empresa-rol/" + rolId + "/metodos")).body(response);
	}

	/**
	 * DELETE: Quitar todos los permisos de módulos seleccionados de un rol
	 */
	@DeleteMapping("/{rolId}/quitar-modulos-permisos")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void quitarModulosPermisos(@PathVariable Long rolId, @RequestBody @Valid AsignarModulosPermisoRequest dto) {
		rolPermisoService.quitarModulosPermisos(rolId, dto.getModulosIds());
	}

	/**
	 * PUT: Reemplazar un permiso individual por otro Caso de uso: "Asigné el permiso X
	 * por error, quiero reemplazarlo por Y"
	 *
	 * Body ejemplo: { "permisoIdActual": 100, "nuevoPermisoId": 105 }
	 */
	@PutMapping("/{rolId}/reemplazar-permiso")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<RolPermisoAsignadoResponse> reemplazarPermiso(@PathVariable Long rolId,
			@RequestBody @Valid ReemplazarPermisoRequest dto) {

		RolPermisoAsignadoResponse response = rolPermisoService.reemplazarPermisoDeEmpresaRol(rolId,
				dto.getPermisoIdActual(), dto.getNuevoPermisoId());

		return ResponseEntity.ok(response);
	}

	/**
	 * PUT: Reemplazar todos los permisos de un módulo por los de otro Caso de uso:
	 * "Asigné el módulo X por error, quiero reemplazarlo completamente por el módulo Y"
	 *
	 * Body ejemplo: { "moduloIdActual": 342, "nuevoModuloId": 340 }
	 */
	@PutMapping("/{rolId}/reemplazar-modulo")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<RolPermisoAsignadoResponse> reemplazarModulo(@PathVariable Long rolId,
			@RequestBody @Valid ReemplazarModuloRequest dto) {

		RolPermisoAsignadoResponse response = rolPermisoService.reemplazarModuloPermisosDeEmpresaRol(rolId,
				dto.getModuloIdActual(), dto.getNuevoModuloId());

		return ResponseEntity.ok(response);
	}

	/**
	 * ============================================================================
	 * MÉTODOS ANTIGUOS (Compatibilidad / Administración individual de permisos)
	 * ============================================================================
	 */

	/**
	 * Obtener permisos de un rol en una empresa
	 */
	@GetMapping("/rol/{rolId}/permisos")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.OK)
	public List<PermisoResponse> getPermisos(
			@PathVariable Long rolId,
			@RequestParam(name = "empresaId", required = false) Long empresaIdParam) {

		Long empresaId = dualAuthResolver.resolveEmpresaId(empresaIdParam);

		return rolPermisoService.getPermisosByEmpresaRol(rolId, empresaId)
			.stream()
			.map(p -> new PermisoResponse(p.getId(), p.getNombre(), p.getAutoridad()))
			.toList();
	}

	/**
	 * POST: Asignar permisos individuales a un rol (permiso a permiso)
	 * 
	 * Funciona para ambos roles:
	 * - ADMINISTRADOR_EMPRESA: No requiere parámetro empresaId (se obtiene del contexto)
	 * - ADMINISTRADOR_SISTEMA: Requiere parámetro empresaId como query param
	 * 
	 * @param rolId ID del rol a asignar permisos
	 * @param dto DTO con lista de IDs de permisos
	 * @param empresaIdParam Parámetro opcional para ADMINISTRADOR_SISTEMA
	 * @return ResponseEntity con estado 201 CREATED
	 */
	@PostMapping("/rol/{rolId}/permisos")
	@PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> asignarPermisosARolDeEmpresa(
			@PathVariable Long rolId,
			@RequestBody @Valid AsignarPermisosRequest dto,
			@RequestParam(name = "empresaId", required = false) Long empresaIdParam) {

		// El resolver detecta automáticamente qué empresaId usar según el rol
		// - ADMIN_EMPRESA: Ignora parámetro, obtiene del contexto
		// - ADMIN_SISTEMA: Requiere parámetro
		Long empresaId = dualAuthResolver.resolveEmpresaId(empresaIdParam);

		// Usar el nuevo método con empresaId explícito
		rolPermisoService.asignarPermisosAEmpresaRolWithEmpresaId(rolId, dto.getPermisosId(), empresaId);

		return ResponseEntity
			.created(URI.create("/api/v1/empresa-rol-permisos/rol/" + rolId + "/permisos"))
			.build();
	}

	/**
	 * Quitar permisos individuales de un rol
	 */
	@DeleteMapping("/rol/{rolId}/permisos/quitar")
	@PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA') or hasRole('ADMINISTRADOR_EMPRESA')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removePermiso(
			@PathVariable Long rolId,
			@RequestBody @Valid AsignarPermisosRequest dto,
			@RequestParam(name = "empresaId", required = false) Long empresaIdParam) {

		Long empresaId = dualAuthResolver.resolveEmpresaId(empresaIdParam);
		rolPermisoService.quitarPermisosDeEmpresaRol(rolId, dto.getPermisosId(), empresaId);
	}

}
