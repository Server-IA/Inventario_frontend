package com.coagronet.rolpermiso.services;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.permiso.Permiso;
import com.coagronet.permiso.repositories.PermisoRepository;
import com.coagronet.rolpermiso.RolPermiso;
import com.coagronet.rolpermiso.dtos.response.ModuloPermisoResponse;
import com.coagronet.rolpermiso.dtos.response.RolPermisoAsignadoResponse;
import com.coagronet.rolpermiso.repositories.RolPermisoRepository;
import com.coagronet.estado.Estado;
import com.coagronet.metodo.repositories.MetodoRepository;
import com.coagronet.user.User;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.coagronet.rolpermiso.dtos.request.ModuloMetodoRequest;

/**
 * <h5>
 *     Servicio para ADMINISTRADOR_EMPRESA
 * </h5>
 * Gestiona la asignación de permisos a roles por módulo.
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


    /**
     * Obtiene módulos disponibles con sus permisos agrupados, con paginación.
     * Optimizado para evitar cargar todos los permisos en memoria.
     * 
     * @param pageable información de paginación (page, size, sort)
     * @return Página de módulos con permisos agrupados
     */
    @Transactional(readOnly = true)
    public Page<ModuloPermisoResponse> getModulosDisponibles(Pageable pageable) {
        // Obtener página de módulos únicos
        Page<Long> moduloIds = permisoRepository.findDistinctModuloIds(pageable);
        
        // Transformar página de IDs de módulos a página de ModuloPermisoResponse
        return moduloIds.map(moduloId -> {
            List<Permiso> permisosModulo = permisoRepository.findPermisosByModuloId(moduloId);
            if (permisosModulo.isEmpty()) return null;
            
            Permiso primerPermiso = permisosModulo.getFirst();
            List<ModuloPermisoResponse.PermisoDTO> permisosDTO = permisosModulo.stream()
                .map(p -> new ModuloPermisoResponse.PermisoDTO(
                    p.getId(),
                    p.getNombre(),
                    p.getAutoridad(),
                    p.getMetodo() != null ? p.getMetodo().getNombre() : null,
                    p.getUri()
                ))
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

    @Transactional(readOnly = true)
    public List<Permiso> getPermisosByEmpresaRol(Long rolId) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

        return rolPermisoRepository.findPermisosByEmpresaRolId(empresaRol.getId());
    }

    /**
     *  MÉTODO PRINCIPAL: Asigna TODOS los permisos de módulos seleccionados a un rol.
     * 
     * @param rolId ID del rol
     * @param modulosIds Lista de IDs de módulos a asignar
     * @return Confirmación de permisos asignados
     */
    @Transactional
    public RolPermisoAsignadoResponse asignarModulosPermisos(
            Long rolId, 
            List<Long> modulosIds) {
        
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        
        // Validar que el rol existe y está activo en la empresa
        EmpresaRol empresaRol = entidadValidatorFacade
            .validarRolDeEmpresaActivo(empresaId, rolId);
        
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
                        .createdAt(OffsetDateTime.now())
                        .build();
                
                rolPermisoRepository.save(rolPermiso);
            }
        });
        
        // Preparar respuesta con confirmación visual
        List<String> modulos = permisos.stream()
            .map(p -> p.getModulo().getNombre())
            .distinct()
            .toList();
        
        List<String> autoridades = permisos.stream()
            .map(Permiso::getAutoridad)
            .toList();
        
        return RolPermisoAsignadoResponse.builder()
            .rolId(empresaRol.getRol().getId())
            .rolNombre(empresaRol.getRol().getNombre())
            .permisosAsignados(permisos.size())
            .modulos(modulos)
            .autoridades(autoridades)
            .build();
    }

        /**
         * Asigna SOLO los permisos de lectura (método GET o autoridad con 'READ')
         * de los módulos seleccionados a un rol.
         */
        @Transactional
        public RolPermisoAsignadoResponse asignarModulosPermisosLectura(
            Long rolId,
            List<Long> modulosIds) {

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        EmpresaRol empresaRol = entidadValidatorFacade
            .validarRolDeEmpresaActivo(empresaId, rolId);

        List<Permiso> permisos = permisoRepository.findPermisosByModulosIds(modulosIds).stream()
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
                .createdAt(OffsetDateTime.now())
                .build();

            rolPermisoRepository.save(rolPermiso);
            }
        });

        List<String> modulos = permisos.stream()
            .map(p -> p.getModulo().getNombre())
            .distinct()
            .toList();

        List<String> autoridades = permisos.stream()
            .map(Permiso::getAutoridad)
            .toList();

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

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

        List<Permiso> permisos = permisoRepository.findAllById(permisoIds);

        for (Permiso permiso : permisos) {
            RolPermiso rolPermiso = RolPermiso.builder()
                    .empresaRol(empresaRol)
                    .permiso(permiso)
                    .build();
            rolPermisoRepository.save(rolPermiso);
        }
    }

    /**
     * Quita permisos de módulos específicos de un rol
     */
    @Transactional
    public void quitarModulosPermisos(Long rolId, List<Long> modulosIds) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        EmpresaRol empresaRol = entidadValidatorFacade
            .validarRolDeEmpresaActivo(empresaId, rolId);
        
        List<Permiso> permisos = permisoRepository.findPermisosByModulosIds(modulosIds);
        List<Long> permisoIds = permisos.stream()
            .map(Permiso::getId)
            .collect(Collectors.toList());
        
        if (!permisoIds.isEmpty()) {
            rolPermisoRepository.deleteByEmpresaRolIdAndPermisoIds(
                empresaRol.getId(),
                permisoIds
            );
        }
    }

    @Transactional
    public void quitarPermisosDeEmpresaRol(Long rolId, List<Long> permisoIds) {

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        EmpresaRol empresaRol =
                entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

        if (permisoIds == null || permisoIds.isEmpty()) {
            return;
        }

        rolPermisoRepository.deleteByEmpresaRolIdAndPermisoIds(
                empresaRol.getId(),
                permisoIds
        );
    }

    /**
     * Asigna permisos de módulos seleccionados filtrando por los métodos indicados por módulo.
     * Cada entrada indica un módulo y la lista de métodos a asignar (o "ALL").
     */
    @Transactional
    public RolPermisoAsignadoResponse asignarModulosPermisosConMetodos(
            Long rolId,
            List<ModuloMetodoRequest> modulosMetodos) {

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        EmpresaRol empresaRol = entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId);

        Set<Long> permisoIdsToAssign = new HashSet<>();
        List<Permiso> permisosToAssign = new ArrayList<>();

        for (ModuloMetodoRequest mm : modulosMetodos) {
            List<Permiso> permisosModulo = permisoRepository.findPermisosByModuloId(mm.getModuloId());
            if (permisosModulo == null || permisosModulo.isEmpty()) continue;

            Set<String> métodos = mm.getMetodos().stream()
                    .map(m -> m == null ? "" : m.trim().toUpperCase())
                    .collect(Collectors.toSet());

            boolean all = métodos.contains("ALL");
            boolean readRequested = métodos.contains("READ");

            for (Permiso p : permisosModulo) {
                boolean include = false;
                if (all) include = true;
                else if (p.getMetodo() != null && métodos.contains(p.getMetodo().getNombre().toUpperCase())) include = true;
                else if (readRequested && ((p.getMetodo() != null && "GET".equalsIgnoreCase(p.getMetodo().getNombre()))
                        || (p.getAutoridad() != null && p.getAutoridad().toUpperCase().contains("READ")))) include = true;

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
                        .createdAt(OffsetDateTime.now())
                        .build();
                rolPermisoRepository.save(rp);
            }
        }

        List<String> modulos = permisosToAssign.stream()
                .map(p -> p.getModulo().getNombre())
                .distinct()
                .collect(Collectors.toList());

        List<String> autoridades = permisosToAssign.stream()
                .map(Permiso::getAutoridad)
                .collect(Collectors.toList());

        return RolPermisoAsignadoResponse.builder()
                .rolId(empresaRol.getRol().getId())
                .rolNombre(empresaRol.getRol().getNombre())
                .permisosAsignados(permisosToAssign.size())
                .modulos(modulos)
                .autoridades(autoridades)
                .build();
    }
}
