package com.coagronet.rolpermiso.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.metodo.repositories.MetodoRepository;
import com.coagronet.modulo.Modulo;
import com.coagronet.permiso.Permiso;
import com.coagronet.permiso.repositories.PermisoRepository;
import com.coagronet.rolpermiso.RolPermiso;
import com.coagronet.rolpermiso.dtos.request.ModuloMetodoRequest;
import com.coagronet.rolpermiso.dtos.response.ModuloPermisoResponse;
import com.coagronet.rolpermiso.dtos.response.RolPermisoAsignadoResponse;
import com.coagronet.rolpermiso.repositories.RolPermisoRepository;
import com.coagronet.user.User;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;

import lombok.RequiredArgsConstructor;

/**
 * <h5>Servicio para ADMINISTRADOR_EMPRESA</h5> Gestiona la asignación de permisos a roles
 * por módulo.
 */
@Service
@RequiredArgsConstructor
public class RolPermisoService {

	private final RolPermisoRepository rolPermisoRepository;

	private final PermisoRepository permisoRepository;

	private final EmpresaRolRepository empresaRolRepository;

	private final MetodoRepository metodoRepository;

	private final UserEmpresaService userEmpresaService;

	private final EntidadValidatorFacade entidadValidatorFacade;

	private final AuthenticationService authenticationService;

	private final EstadoRepository estadoRepository; // Kept just in case you use it
														// elsewhere, though it's no
														// longer strictly needed here.

	/**
	 * Obtiene módulos disponibles con sus permisos agrupados, con paginación. Optimizado
	 * para evitar cargar todos los permisos en memoria.
	 * @param pageable información de paginación (page, size, sort)
	 * @return Página de módulos con permisos agrupados
	 */
	@Transactional(readOnly = true)
	public Page<ModuloPermisoResponse> getModulosDisponibles(Pageable pageable) {

		// 1. Obtener solo los IDs de la página actual (1 consulta)
		Page<Long> moduloIdsPage = permisoRepository.findDistinctModuloIds(pageable);

		if (moduloIdsPage.isEmpty()) {
			return Page.empty(pageable);
		}

		// 2. Traer TODOS los permisos, módulos y métodos de esos IDs (1 consulta gigante
		// y optimizada)
		List<Permiso> todosLosPermisos = permisoRepository
			.findPermisosConRelacionesByModuloIdIn(moduloIdsPage.getContent());

		// 3. Agrupar los permisos por Modulo ID en la memoria de Java (Ultra rápido)
		Map<Long, List<Permiso>> permisosPorModulo = todosLosPermisos.stream()
			.collect(Collectors.groupingBy(p -> p.getModulo().getId()));

		// 4. Mapear y construir la respuesta sin tocar la base de datos de nuevo
		return moduloIdsPage.map(moduloId -> {

			List<Permiso> permisosModulo = permisosPorModulo.getOrDefault(moduloId, Collections.emptyList());
			if (permisosModulo.isEmpty())
				return null;

			Permiso primerPermiso = permisosModulo.get(0);

			// Esto ya no dispara consultas gracias al JOIN FETCH
			List<ModuloPermisoResponse.PermisoDTO> permisosDTO = permisosModulo.stream()
				.map(p -> new ModuloPermisoResponse.PermisoDTO(p.getId(), p.getNombre(), p.getAutoridad(),
						p.getMetodo() != null ? p.getMetodo().getNombre() : null, // Sin
																					// N+1
						p.getUri()))
				.toList();

			return ModuloPermisoResponse.builder()
				.moduloId(moduloId)
				.moduloNombre(primerPermiso.getModulo().getNombre())
				.moduloUrl(primerPermiso.getModulo().getUrl())
				.moduloDescripcion(primerPermiso.getModulo().getDescripcion())
				.moduloIcon(primerPermiso.getModulo().getIcon())
				.permisos(permisosDTO)
				.build();
		});
	}

	/**
	 * Implementa el patrón Unit of Work en modo solo lectura. Según la documentación de
	 * Spring Data Access, readOnly = true optimiza el rendimiento al deshabilitar el
	 * dirty-checking (flushing) de Hibernate, ahorrando CPU y memoria.
	 */
	@Transactional(readOnly = true)
	public List<ModuloPermisoResponse> getModulosBySubsistemas(List<Long> subsistemaIds) {

		// 1. Unica ida a la base de datos
		List<Permiso> permisosAsignables = permisoRepository
			.findPermisosActivosAdminEmpresaBySubsistemas(subsistemaIds);

		// 2. Agrupación en memoria
		Map<Modulo, List<Permiso>> permisosPorModulo = permisosAsignables.stream()
			.collect(Collectors.groupingBy(Permiso::getModulo));

		// 3. Transformación al DTO de salida
		return permisosPorModulo.entrySet().stream().map(entry -> {
			Modulo modulo = entry.getKey();
			List<Permiso> permisosDelModulo = entry.getValue();

			List<ModuloPermisoResponse.PermisoDTO> permisosDTO = permisosDelModulo.stream()
				.map(p -> new ModuloPermisoResponse.PermisoDTO(p.getId(), p.getNombre(), p.getAutoridad(),
						p.getMetodo() != null ? p.getMetodo().getNombre() : null, p.getUri()))
				.toList();

			return ModuloPermisoResponse.builder()
				.moduloId(modulo.getId())
				.moduloNombre(modulo.getNombre())
				.moduloUrl(modulo.getUrl())
				.moduloDescripcion(modulo.getDescripcion())
				.moduloIcon(modulo.getIcon())
				.permisos(permisosDTO)
				.build();
		}).sorted(Comparator.comparing(ModuloPermisoResponse::getModuloNombre)).toList();
	}

	@Transactional(readOnly = true)
	public List<Permiso> getPermisosByEmpresaRol(Long rolId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		return rolPermisoRepository.findPermisosByEmpresaRolId(empresaRol.getId());
	}

	/**
	 * MÉTODO PRINCIPAL: Asigna TODOS los permisos de módulos seleccionados a un rol.
	 * @param rolId ID del rol
	 * @param modulosIds Lista de IDs de módulos a asignar
	 * @return Confirmación de permisos asignados
	 */
	@Transactional
	public RolPermisoAsignadoResponse asignarModulosPermisos(Long rolId, List<Long> modulosIds) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		// Validar que el rol existe y está activo en la empresa
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		// Obtener TODOS los permisos de los módulos seleccionados
		List<Permiso> permisos = permisoRepository.findPermisosByModulosIds(modulosIds);

		if (permisos.isEmpty()) {
			throw new RuntimeException("No se encontraron permisos para los módulos seleccionados");
		}

		// Obtener estado activo y usuario actual
		Estado estadoActivo = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
		User currentUser = authenticationService.getAuthenticatedUser();

		// Asignar cada permiso al rol
		permisos.forEach(permiso -> {
			// Evitar duplicados
			if (!rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(empresaRol.getId(), permiso.getId())) {
				RolPermiso rolPermiso = RolPermiso.builder()
					.empresaRol(empresaRol)
					.permiso(permiso)
					.estado(estadoActivo)
					.createdBy(currentUser)
					.createdAt(Instant.now())
					.build();

				rolPermisoRepository.save(rolPermiso);
			}
		});

		// Preparar respuesta con confirmación visual
		List<String> modulos = permisos.stream().map(p -> p.getModulo().getNombre()).distinct().toList();

		List<String> autoridades = permisos.stream().map(Permiso::getAutoridad).toList();

		return RolPermisoAsignadoResponse.builder()
			.rolId(empresaRol.getRol().getId())
			.rolNombre(empresaRol.getRol().getNombre())
			.permisosAsignados(permisos.size())
			.modulos(modulos)
			.autoridades(autoridades)
			.build();
	}

	/**
	 * Asigna SOLO los permisos de lectura (método GET o autoridad con 'READ') de los
	 * módulos seleccionados a un rol.
	 */
	@Transactional
	public RolPermisoAsignadoResponse asignarModulosPermisosLectura(Long rolId, List<Long> modulosIds) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		List<Permiso> permisos = permisoRepository.findPermisosByModulosIds(modulosIds)
			.stream()
			.filter(p -> (p.getMetodo() != null && "GET".equalsIgnoreCase(p.getMetodo().getNombre()))
					|| (p.getAutoridad() != null && p.getAutoridad().toUpperCase().contains("READ")))
			.collect(Collectors.toList());

		if (permisos.isEmpty()) {
			throw new RuntimeException("No se encontraron permisos de lectura para los módulos seleccionados");
		}

		Estado estadoActivo = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
		User currentUser = authenticationService.getAuthenticatedUser();

		permisos.forEach(permiso -> {
			if (!rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(empresaRol.getId(), permiso.getId())) {
				RolPermiso rolPermiso = RolPermiso.builder()
					.empresaRol(empresaRol)
					.permiso(permiso)
					.estado(estadoActivo)
					.createdBy(currentUser)
					.createdAt(Instant.now())
					.build();

				rolPermisoRepository.save(rolPermiso);
			}
		});

		List<String> modulos = permisos.stream().map(p -> p.getModulo().getNombre()).distinct().toList();

		List<String> autoridades = permisos.stream().map(Permiso::getAutoridad).toList();

		return RolPermisoAsignadoResponse.builder()
			.rolId(empresaRol.getRol().getId())
			.rolNombre(empresaRol.getRol().getNombre())
			.permisosAsignados(permisos.size())
			.modulos(modulos)
			.autoridades(autoridades)
			.build();
	}

	@Transactional
	public void asignarPermisosAEmpresaRol(Long rolId, List<Long> permisoIds) {
		if (permisoIds == null || permisoIds.isEmpty())
			return;

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);
		Estado estadoActivo = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
		User currentUser = authenticationService.getAuthenticatedUser();

		// 1. Verificación de Roles mediante Spring Security Context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isSystemAdmin = authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.anyMatch(role -> role.equals("ROLE_ADMINISTRADOR_SISTEMA") || role.equals("ADMINISTRADOR_SISTEMA"));

		// 2. Extraer permisos válidos de DB
		List<Permiso> permisos = isSystemAdmin ? permisoRepository.findAllById(permisoIds)
				: permisoRepository.findByIdInAndAdminEmpresaTrue(permisoIds);

		if (permisos.isEmpty())
			return;

		// 3. OPTIMIZACIÓN: Extraer IDs de permisos ya asignados en 1 sola consulta
		List<Long> validPermisoIds = permisos.stream().map(Permiso::getId).toList();
		Set<Long> permisosYaAsignados = rolPermisoRepository
			.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(empresaRol.getId(), validPermisoIds);

		// 4. Filtrado en memoria y construcción de Entidades
		List<RolPermiso> nuevosPermisos = permisos.stream()
			.filter(permiso -> !permisosYaAsignados.contains(permiso.getId()))
			.map(permiso -> RolPermiso.builder()
				.empresaRol(empresaRol)
				.permiso(permiso)
				.estado(estadoActivo)
				.createdBy(currentUser)
				.createdAt(Instant.now())
				.build())
			.toList();

		// 5. Inserción por lotes (Unit of Work via @Transactional)
		if (!nuevosPermisos.isEmpty()) {
			rolPermisoRepository.saveAll(nuevosPermisos);
		}
	}

	/**
	 * Quita permisos de módulos específicos de un rol
	 */
	@Transactional
	public void quitarModulosPermisos(Long rolId, List<Long> modulosIds) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		List<Permiso> permisos = permisoRepository.findPermisosByModulosIds(modulosIds);
		List<Long> permisoIds = permisos.stream().map(Permiso::getId).collect(Collectors.toList());

		if (!permisoIds.isEmpty()) {
			rolPermisoRepository.deleteByEmpresaRolIdAndPermisoIds(empresaRol.getId(), permisoIds);
		}
	}

	/**
	 * Reemplaza un permiso individual por otro en la asignación de un rol-empresa. Caso
	 * de uso: "asigné el permiso X por error, quiero reemplazarlo por Y"
	 * @param rolId ID del rol
	 * @param permisoIdActual ID del permiso que queremos quitar
	 * @param nuevoPermisoId ID del nuevo permiso a asignar
	 * @throws RuntimeException si el permiso actual no está asignado o si el nuevo ya
	 * existe
	 */
	@Transactional
	public RolPermisoAsignadoResponse reemplazarPermisoDeEmpresaRol(Long rolId, Long permisoIdActual,
			Long nuevoPermisoId) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		// Validar que el permiso actual existe y está asignado
		if (!rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(empresaRol.getId(), permisoIdActual)) {
			throw new RuntimeException("El permiso actual no está asignado a este rol-empresa");
		}

		// Validar que el nuevo permiso no está ya asignado (evitar duplicados)
		if (rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(empresaRol.getId(), nuevoPermisoId)) {
			throw new RuntimeException("El nuevo permiso ya está asignado a este rol-empresa");
		}

		// Quitar el permiso antiguo
		rolPermisoRepository.deleteByEmpresaRolIdAndPermisoIds(empresaRol.getId(), List.of(permisoIdActual));

		// Asignar el nuevo permiso
		Estado estadoActivo = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
		User currentUser = authenticationService.getAuthenticatedUser();
		Permiso nuevoPermiso = permisoRepository.findById(nuevoPermisoId)
			.orElseThrow(() -> new RuntimeException("Permiso destino no encontrado con ID: " + nuevoPermisoId));

		RolPermiso rolPermiso = RolPermiso.builder()
			.empresaRol(empresaRol)
			.permiso(nuevoPermiso)
			.estado(estadoActivo)
			.createdBy(currentUser)
			.createdAt(Instant.now())
			.build();
		rolPermisoRepository.save(rolPermiso);

		return RolPermisoAsignadoResponse.builder()
			.rolId(empresaRol.getRol().getId())
			.rolNombre(empresaRol.getRol().getNombre())
			.permisosAsignados(1)
			.modulos(List.of(nuevoPermiso.getModulo().getNombre()))
			.autoridades(List.of(nuevoPermiso.getAutoridad()))
			.build();
	}

	/**
	 * Reemplaza todos los permisos de un módulo por los permisos de otro módulo. Caso de
	 * uso: "asigné el módulo X por error, quiero reemplazarlo completamente por el módulo
	 * Y"
	 * @param rolId ID del rol
	 * @param moduloIdActual ID del módulo que queremos quitar
	 * @param nuevoModuloId ID del nuevo módulo a asignar
	 * @throws RuntimeException si el módulo actual no tiene permisos asignados o si hay
	 * conflictos
	 */
	@Transactional
	public RolPermisoAsignadoResponse reemplazarModuloPermisosDeEmpresaRol(Long rolId, Long moduloIdActual,
			Long nuevoModuloId) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		// Obtener permisos del módulo actual asignados a este rol
		List<Permiso> permisosActuales = permisoRepository.findPermisosByModuloId(moduloIdActual);
		if (permisosActuales.isEmpty()) {
			throw new RuntimeException("No hay permisos del módulo actual asignados a este rol");
		}

		// Filtrar solo los que están asignados a este rol-empresa
		List<Long> permisoIdsAsignados = permisosActuales.stream()
			.filter(p -> rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(empresaRol.getId(), p.getId()))
			.map(Permiso::getId)
			.toList();

		if (permisoIdsAsignados.isEmpty()) {
			throw new RuntimeException("Ningún permiso del módulo actual está asignado a este rol-empresa");
		}

		// Obtener permisos del nuevo módulo
		List<Permiso> permisosNuevos = permisoRepository.findPermisosByModuloId(nuevoModuloId);
		if (permisosNuevos.isEmpty()) {
			throw new RuntimeException("El nuevo módulo no tiene permisos disponibles");
		}

		// Quitar todos los permisos del módulo actual
		rolPermisoRepository.deleteByEmpresaRolIdAndPermisoIds(empresaRol.getId(), permisoIdsAsignados);

		// Asignar todos los permisos del nuevo módulo (evitando duplicados)
		Estado estadoActivo = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
		User currentUser = authenticationService.getAuthenticatedUser();

		int permisosAsignados = 0;
		for (Permiso permiso : permisosNuevos) {
			if (!rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(empresaRol.getId(), permiso.getId())) {
				RolPermiso rolPermiso = RolPermiso.builder()
					.empresaRol(empresaRol)
					.permiso(permiso)
					.estado(estadoActivo)
					.createdBy(currentUser)
					.createdAt(Instant.now())
					.build();
				rolPermisoRepository.save(rolPermiso);
				permisosAsignados++;
			}
		}

		List<String> modulos = permisosNuevos.stream().map(p -> p.getModulo().getNombre()).distinct().toList();

		List<String> autoridades = permisosNuevos.stream().map(Permiso::getAutoridad).toList();

		return RolPermisoAsignadoResponse.builder()
			.rolId(empresaRol.getRol().getId())
			.rolNombre(empresaRol.getRol().getNombre())
			.permisosAsignados(permisosAsignados)
			.modulos(modulos)
			.autoridades(autoridades)
			.build();
	}

	@Transactional
	public void quitarPermisosDeEmpresaRol(Long rolId, List<Long> permisoIds) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		if (permisoIds == null || permisoIds.isEmpty()) {
			return;
		}

		rolPermisoRepository.deleteByEmpresaRolIdAndPermisoIds(empresaRol.getId(), permisoIds);
	}

	/**
	 * Asigna permisos de módulos seleccionados filtrando por los métodos indicados por
	 * módulo. Cada entrada indica un módulo y la lista de métodos a asignar (o "ALL").
	 */
	@Transactional
	public RolPermisoAsignadoResponse asignarModulosPermisosConMetodos(Long rolId,
			List<ModuloMetodoRequest> modulosMetodos) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

		Set<Long> permisoIdsToAssign = new HashSet<>();
		List<Permiso> permisosToAssign = new ArrayList<>();

		for (ModuloMetodoRequest mm : modulosMetodos) {
			List<Permiso> permisosModulo = permisoRepository.findPermisosByModuloId(mm.getModuloId());
			if (permisosModulo == null || permisosModulo.isEmpty())
				continue;

			Set<String> métodos = mm.getMetodos()
				.stream()
				.map(m -> m == null ? "" : m.trim().toUpperCase())
				.collect(Collectors.toSet());

			boolean all = métodos.contains("ALL");
			boolean readRequested = métodos.contains("READ");

			for (Permiso p : permisosModulo) {
				boolean include = false;
				if (all)
					include = true;
				else if (p.getMetodo() != null && métodos.contains(p.getMetodo().getNombre().toUpperCase()))
					include = true;
				else if (readRequested && ((p.getMetodo() != null && "GET".equalsIgnoreCase(p.getMetodo().getNombre()))
						|| (p.getAutoridad() != null && p.getAutoridad().toUpperCase().contains("READ"))))
					include = true;

				if (include && !permisoIdsToAssign.contains(p.getId())) {
					permisoIdsToAssign.add(p.getId());
					permisosToAssign.add(p);
				}
			}
		}

		if (permisosToAssign.isEmpty()) {
			throw new RuntimeException("No se encontraron permisos para los módulos/metodos solicitados");
		}

		Estado estadoActivo = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
		User currentUser = authenticationService.getAuthenticatedUser();

		for (Permiso permiso : permisosToAssign) {
			if (!rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(empresaRol.getId(), permiso.getId())) {
				RolPermiso rp = RolPermiso.builder()
					.empresaRol(empresaRol)
					.permiso(permiso)
					.estado(estadoActivo)
					.createdBy(currentUser)
					.createdAt(Instant.now())
					.build();
				rolPermisoRepository.save(rp);
			}
		}

		List<String> modulos = permisosToAssign.stream()
			.map(p -> p.getModulo().getNombre())
			.distinct()
			.collect(Collectors.toList());

		List<String> autoridades = permisosToAssign.stream().map(Permiso::getAutoridad).collect(Collectors.toList());

		return RolPermisoAsignadoResponse.builder()
			.rolId(empresaRol.getRol().getId())
			.rolNombre(empresaRol.getRol().getNombre())
			.permisosAsignados(permisosToAssign.size())
			.modulos(modulos)
			.autoridades(autoridades)
			.build();
	}

}