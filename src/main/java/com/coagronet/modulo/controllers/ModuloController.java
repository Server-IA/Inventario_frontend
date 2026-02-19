package com.coagronet.modulo.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.services.MenuService;
import com.coagronet.modulo.dtos.ModuloDetailResponse;
import com.coagronet.modulo.dtos.ModuloRequeridoPatch;
import com.coagronet.modulo.dtos.ModuloRequest;
import com.coagronet.modulo.dtos.ModuloSummaryResponse;
import com.coagronet.modulo.services.ModuloService;

import jakarta.validation.Valid;

/**
 * Controlador REST encargado de exponer los servicios de gestión para el recurso Módulo.
 * <p>
 * Define los puntos de entrada (endpoints) bajo la ruta <code>/api/v1/modulos</code>, permitiendo la interacción de
 * clientes externos con la lógica de negocio. Actúa como capa de presentación, delegando el procesamiento al
 * {@link ModuloService} y orquestando las respuestas HTTP estandarizadas.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see ModuloService
 * @since 2026
 */
@RestController @RequestMapping("/api/v2/modulos")
public class ModuloController {

    private final ModuloService moduloService;
    private final MenuService menuService;

    public ModuloController(ModuloService moduloService, MenuService menuService) {
        this.moduloService = moduloService;
        this.menuService = menuService;
    }

    /**
     * Registra un nuevo módulo en la plataforma procesando una solicitud HTTP POST.
     * <p>
     * Este método recibe un payload JSON validado contra el esquema {@link ModuloRequest}. Si la operación es exitosa,
     * retorna un estado <code>201 Created</code> e incluye la cabecera <code>Location</code> con la URI del recurso
     * recién creado, siguiendo las mejores prácticas RESTful.
     * </p>
     * <p>
     * Las excepciones de negocio como duplicidad o referencias inexistentes son propagadas y manejadas globalmente por
     * el <code>Advice</code> de excepciones.
     * </p>
     *
     * @param request objeto de transferencia (DTO) que contiene los datos del módulo. Debe cumplir con las validaciones
     * (<code>@Valid</code>) de obligatoriedad y formato.
     * @return una respuesta {@link ResponseEntity} sin cuerpo (Void), con estado HTTP 201 y la cabecera de ubicación
     * del recurso.
     * @see ModuloService#crearModulo(ModuloRequest)
     * @see com.coagronet.exceptionHandler.RecursoDuplicadoException
     * @see com.coagronet.exceptionHandler.EntidadNoEncontradaException
     */
    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody ModuloRequest request) {
        Long id = moduloService.crearModulo(request);
        return ResponseEntity.created(URI.create("/api/v2/modulos/" + id)).build();
    }

    /**
     * Actualiza la información de un módulo existente procesando una petición HTTP PUT.
     * <p>
     * Este método actúa como punto de entrada para la modificación de recursos. Valida la estructura del cuerpo de la
     * solicitud ({@link ModuloRequest}) y delega la ejecución de la lógica de negocio al servicio subyacente.
     * </p>
     * <p>
    * Si la operación es exitosa, responde con un estado <strong>204 No Content</strong>, indicando que la solicitud ha
    * sido procesada correctamente y no requiere retornar contenido adicional.
     * </p>
     *
     * @param id identificador único del módulo a modificar, capturado desde la variable de ruta (Path Variable).
     * @param entity objeto de transferencia (DTO) con los nuevos datos del módulo. Debe cumplir con las validaciones de
     * formato y obligatoriedad definidas (<code>@Valid</code>).
    * @return una instancia de {@link ResponseEntity} con estado HTTP 204 y sin cuerpo de respuesta.
     * @throws com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException si el <code>id</code> proporcionado no
     * corresponde a ningún módulo.
     * @throws com.coagronet.exceptionHandler.custom.RecursoDuplicadoException si el nuevo nombre del módulo entra en
     * conflicto con otro registro existente.
     * @throws com.coagronet.exceptionHandler.custom.EntidadNoEncontradaException si las referencias a entidades
     * relacionadas (Estado, SubSistema) son inválidas.
     * @see ModuloService#actualizarModulo(Long, ModuloRequest)
     * @since 2026
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody ModuloRequest entity) {
        moduloService.actualizarModulo(id, entity);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recupera el listado paginado de módulos registrados en el sistema.
     * <p>
     * Este endpoint procesa peticiones HTTP GET para consultar el catálogo de módulos. Utiliza la estrategia de
     * proyección de datos ({@link com.coagronet.subsistema.dto.ModuloResponse}) para entregar una respuesta optimizada,
     * donde las relaciones con otras entidades (Estado, Subsistema, Tipos) ya vienen resueltas con sus nombres
     * descriptivos.
     * </p>
     * <p>
     * Soporta los parámetros estándar de paginación y ordenamiento definidos por Spring Data (ej.
     * <code>?page=0&size=10&sort=nombre,asc</code>).
     * </p>
     *
     * @param pageable interfaz que captura la información de paginación (número de página, tamaño) y los criterios de
     * ordenamiento enviados en la URL de la solicitud.
     * @return una instancia de {@link ResponseEntity} con estado <strong>200 OK</strong>. El cuerpo de la respuesta
     * contiene un objeto {@link org.springframework.data.domain.Page} con la lista de módulos encontrados.
     * @see com.coagronet.subsistema.services.ModuloService#obtenerModulos(Pageable)
     * @see com.coagronet.subsistema.dto.ModuloResponse
     * @since 2026
     */
    @GetMapping
    public ResponseEntity<?> obtenerModulos(Pageable pageable) {
        return ResponseEntity.ok(moduloService.obtenerModulos(pageable));
    }

    /**
     * Recupera la información detallada de un módulo específico por su identificador único.
     * <p>
     * Este endpoint expone el recurso de lectura individual utilizando la proyección {@link ModuloDetailResponse}. Está
     * diseñado para suministrar los datos necesarios para cargar formularios de edición en el cliente, incluyendo los
     * identificadores de las relaciones (Estado, Subsistema, Tipos) para el enlace de datos (data binding).
     * </p>
     * <p>
     * Responde con un estado <strong>200 OK</strong> si el recurso existe. En caso contrario, el manejo de excepciones
     * global retornará un estado <strong>404 Not Found</strong>.
     * </p>
     *
     * @param id identificador numérico del módulo a consultar, extraído de la variable de ruta (URI).
     * @return una instancia de {@link ResponseEntity} que envuelve el objeto {@link ModuloDetailResponse} con los datos
     * del módulo solicitado.
     * @throws com.coagronet.exceptionHandler.RecursoNoEncontradoException si el id proporcionado no corresponde a
     * ningún módulo registrado.
     * @see com.coagronet.subsistema.services.ModuloService#obtenerDetalleModulo(Long)
     * @see ModuloDetailResponse
     * @since 2026
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModuloDetailResponse> obtenerDetalleModulo(@PathVariable Long id) {
        ModuloDetailResponse response = moduloService.obtenerDetalleModulo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Recupera el listado de módulos disponibles para la conformación del menú de la empresa actual.
     * <p>
     * Este endpoint maneja las peticiones GET filtradas por el parámetro <code>disponiblesParaMenu=true</code>. Delega
     * la lógica de negocio al servicio correspondiente para obtener los datos estructurados.
     * </p>
     *
     * @return Un objeto {@link ResponseEntity} que contiene una lista de {@link MenuSubSistemaResponseDTO} con los
     * módulos disponibles y el estado HTTP 200 (OK).
     */
    @GetMapping(params = "disponiblesParaMenu=true")
    public ResponseEntity<List<MenuSubSistemaResponseDTO>> getModulosDisponibles() {
        List<MenuSubSistemaResponseDTO> response = menuService.obtenerModulosDisponiblesParaEmpresa();
        return ResponseEntity.ok(response);
    }

    /**
     * Procesa una solicitud de actualización parcial para los atributos de un módulo específico.
     * <p>
     * Este endpoint maneja peticiones HTTP PATCH. Valida el cuerpo de la solicitud (DTO) y delega la persistencia al
     * servicio. Retorna la representación actualizada del recurso.
     * </p>
     *
     * @param id El identificador único del módulo a modificar. Proveniente del path de la URL.
     * @param patchDTO El objeto de transferencia de datos {@link ModuloRequeridoPatch} que contiene los campos
     * modificables (ej. bandera de requerido). Debe cumplir con las validaciones vigentes.
     * @return Un objeto {@link ResponseEntity} que contiene el {@link ModuloSummaryResponse} actualizado y el estado
     * HTTP 200 (OK).
     * @see ModuloService#actualizarRequerido(Long, ModuloRequeridoPatch)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ModuloSummaryResponse> actualizarParcial(@PathVariable Long id,
            @RequestBody @Valid ModuloRequeridoPatch patchDTO) {

        return ResponseEntity.ok(moduloService.actualizarRequerido(id, patchDTO));
    }

}
