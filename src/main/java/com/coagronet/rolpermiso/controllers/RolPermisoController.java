package com.coagronet.rolpermiso.controllers;

import com.coagronet.rolpermiso.dtos.request.AsignarModulosPermisoRequest;
import com.coagronet.rolpermiso.dtos.request.AsignarModulosMetodosRequest;
import com.coagronet.rolpermiso.dtos.request.AsignarPermisosRequest;
import com.coagronet.rolpermiso.dtos.response.ModuloPermisoResponse;
import com.coagronet.rolpermiso.dtos.response.PermisoResponse;
import com.coagronet.rolpermiso.dtos.response.RolPermisoAsignadoResponse;
import com.coagronet.rolpermiso.services.RolPermisoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * <h5>
 *     CONTROLLER para ADMINISTRADOR_EMPRESA
 * </h5>
 * Gestiona la asignación de permisos a roles, con soporte para seleccionar por módulos.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/empresa-rol")
public class RolPermisoController {

    private final RolPermisoService rolPermisoService;

    /**
     * GET: Obtener módulos disponibles con sus permisos agrupados
     * Admin de empresa usa esto para ver qué módulos puede asignar a los roles
     */
    @GetMapping("/modulos-disponibles")
    @PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ModuloPermisoResponse>> getModulosDisponibles() {
        return ResponseEntity.ok(rolPermisoService.getModulosDisponibles());
    }

    /**
     * POST: Asignar TODOS los permisos de módulos seleccionados a un rol
     * 
    * Body ejemplo (el `rolId` va en la URL como path variable):
    * {
    *   "modulosIds": [342, 340]
    * }
     * 
     * Resultado: Se asignan todos los permisos donde modulo_id IN (342, 340) al rol
     */
    @PostMapping("/{rolId}/asignar-modulos-permisos")
    @PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RolPermisoAsignadoResponse> asignarModulosPermisos(
            @PathVariable Long rolId,
            @RequestBody @Valid AsignarModulosPermisoRequest dto) {
        
        RolPermisoAsignadoResponse response = rolPermisoService
            .asignarModulosPermisos(rolId, dto.getModulosIds());
        
        return ResponseEntity
            .created(URI.create("/api/v1/empresa-rol/" + rolId))
            .body(response);
    }

    /**
     * POST: Asignar SOLO permisos de lectura (GET/READ) de los módulos seleccionados
     *
    * Body ejemplo (el `rolId` va en la URL como path variable):
    * {
    *   "modulosIds": [342, 340]
    * }
     */
    @PostMapping("/{rolId}/asignar-modulos-lectura")
    @PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RolPermisoAsignadoResponse> asignarModulosPermisosLectura(
            @PathVariable Long rolId,
            @RequestBody @Valid AsignarModulosPermisoRequest dto) {

        RolPermisoAsignadoResponse response = rolPermisoService
            .asignarModulosPermisosLectura(rolId, dto.getModulosIds());

        return ResponseEntity
            .created(URI.create("/api/v1/empresa-rol/" + rolId + "/lectura"))
            .body(response);
    }

        /**
         * POST: Asignar permisos de módulos seleccionados filtrando por métodos indicados por módulo.
         * Body ejemplo (el `rolId` va en la URL como path variable):
         * {
         *   "modulosMetodos": [
         *     {"moduloId": 342, "metodos": ["GET","POST"]},
         *     {"moduloId": 340, "metodos": ["ALL"]}
         *   ]
         * }
         */
        @PostMapping("/{rolId}/asignar-modulos-metodos")
        @PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
        @ResponseStatus(HttpStatus.CREATED)
        public ResponseEntity<RolPermisoAsignadoResponse> asignarModulosPermisosConMetodos(
            @PathVariable Long rolId,
            @RequestBody @Valid AsignarModulosMetodosRequest dto) {

        RolPermisoAsignadoResponse response = rolPermisoService
            .asignarModulosPermisosConMetodos(rolId, dto.getModulosMetodos());

        return ResponseEntity
            .created(URI.create("/api/v1/empresa-rol/" + rolId + "/metodos"))
            .body(response);
        }

    /**
     * DELETE: Quitar todos los permisos de módulos seleccionados de un rol
     */
    @DeleteMapping("/{rolId}/quitar-modulos-permisos")
    @PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarModulosPermisos(
            @PathVariable Long rolId,
            @RequestBody @Valid AsignarModulosPermisoRequest dto) {
        rolPermisoService.quitarModulosPermisos(rolId, dto.getModulosIds());
    }

    /**
     * ============================================================================
     * MÉTODOS ANTIGUOS (Compatibilidad / Administración individual de permisos)
     * ============================================================================
     */

    /**
     * Obtener permisos de un rol en una empresa (por permiso individual)
     * @deprecated Usar getModulosDisponibles() en su lugar
     */
    @GetMapping("/{empresaId}/rol/{rolId}/permisos")
    @PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
    @ResponseStatus(HttpStatus.OK)
    @Deprecated(forRemoval = false, since = "1.0")
    public List<PermisoResponse> getPermisos(
            @PathVariable Long empresaId,
            @PathVariable Long rolId) {

        return rolPermisoService.getPermisosByEmpresaRol(rolId)
                .stream()
                .map(p -> new PermisoResponse(p.getId(), p.getNombre(), p.getAutoridad()))
                .toList();
    }

    /**
     * Asignar permisos individuales a un rol (permiso a permiso)
     * @deprecated Usar asignarModulosPermisos() en su lugar
     */
    @PostMapping("/rol/permisos")
    @PreAuthorize("hasRole('ADMINISTRADOR_EMPRESA') or hasRole('ADMINISTRADOR_SISTEMA')")
    @ResponseStatus(HttpStatus.CREATED)
    @Deprecated(forRemoval = false, since = "1.0")
    public Void asignarPermisosARolDeEmpresa(
            @RequestBody @Valid AsignarPermisosRequest dto) {

        rolPermisoService.asignarPermisosAEmpresaRol(dto.getRolId(), dto.getPermisosId());
        return null;
    }

    /**
     * Quitar permisos individuales de un rol
     * @deprecated Usar quitarModulosPermisos() en su lugar
     */
    @DeleteMapping("/rol/permisos/quitar")
    @PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA') or hasRole('ADMINISTRADOR_EMPRESA')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(forRemoval = false, since = "1.0")
    public void removePermiso(@RequestBody @Valid AsignarPermisosRequest dto) {
        rolPermisoService.quitarPermisosDeEmpresaRol(dto.getRolId(), dto.getPermisosId());
    }
}
