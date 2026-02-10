package com.coagronet.modulo.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.modulo.dtos.ModuloRequest;
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
@RestController @RequestMapping("/api/v1/modulos")
public class ModuloController {

    private final ModuloService moduloService;

    /**
     * Inicializa el controlador inyectando la dependencia de servicio requerida.
     *
     * @param moduloService componente de lógica de negocio para la gestión de módulos. Gestionado por el contenedor de
     * Spring.
     */
    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
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
        return ResponseEntity.created(URI.create("/api/v1/modulos/" + id)).build();
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
}
