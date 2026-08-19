package com.inventario.subsistema.controllers;

import java.util.List;
import java.util.Objects;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.subsistema.SubSistema;
import com.inventario.subsistema.services.SubSistemaService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Controlador REST encargado de exponer los recursos relacionados con la
 * entidad {@link SubSistema}.
 * <p>
 * Esta clase define los puntos de entrada (endpoints) HTTP bajo la ruta
 * <code>/api/v1/sub-sistemas</code>
 * para la consulta y gestión de los módulos funcionales de la plataforma
 * Inventario.
 * </p>
 * <p>
 * Implementa capacidades de <strong>filtrado dinámico</strong> (Squiggly filter
 * pattern) permitiendo
 * al cliente decidir qué campos de la respuesta JSON desea recibir, optimizando
 * así el ancho de banda
 * y la serialización de datos.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see SubSistemaService
 * @see SubSistema
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/sub-sistemas")
public class SubSistemaController {

    private final SubSistemaService subSistemaService;

    /**
     * Construye una nueva instancia del controlador inyectando sus dependencias
     * requeridas.
     * <p>
     * Se recomienda la inyección por constructor para garantizar la inmutabilidad
     * de las dependencias
     * y facilitar las pruebas unitarias.
     * </p>
     *
     * @param subSistemaService servicio de lógica de negocio para la gestión de
     *                          subsistemas.
     *                          No debe ser <code>null</code>.
     */
    public SubSistemaController(SubSistemaService subSistemaService) {
        this.subSistemaService = subSistemaService;
    }

    /**
     * Recupera el listado general de subsistemas aplicando un filtro dinámico de
     * atributos en la respuesta.
     * <p>
     * Este método consulta todos los registros disponibles a través del servicio y
     * envuelve el resultado
     * en un objeto {@link MappingJacksonValue}. Si el cliente especifica el
     * parámetro <code>campos</code>,
     * la respuesta JSON incluirá únicamente las propiedades solicitadas; de lo
     * contrario, se retornará
     * la estructura completa de la entidad.
     * </p>
     * <p>
     * Ejemplo de uso: <code>GET /api/v1/sub-sistemas?campos=id,nombre</code>
     * </p>
     *
     * @param campos cadena de texto opcional que contiene los nombres de los
     *               atributos a incluir en la
     *               serialización, separados por comas. Si es <code>null</code> o
     *               está vacío, se aplica
     *               la serialización por defecto.
     * @return un contenedor {@link MappingJacksonValue} que incluye la lista de
     *         subsistemas y la
     *         configuración del filtro "filtroDinamico" aplicado.
     * @throws NullPointerException si la lista retornada por el servicio es nula
     *                              (se valida mediante
     *                              {@link Objects#requireNonNull}).
     * @see SimpleBeanPropertyFilter#filterOutAllExcept(String...)
     */
    @GetMapping
    public MappingJacksonValue findAll(@RequestParam(required = false) String campos) {

        List<SubSistema> lista = subSistemaService.findAll();

        MappingJacksonValue wrapper = new MappingJacksonValue(Objects.requireNonNull(lista));

        // 3. Lógica del filtro
        SimpleBeanPropertyFilter filter;
        if (campos != null && !campos.isBlank()) {
            String[] camposArray = campos.replace(" ", "").split(",");
            filter = SimpleBeanPropertyFilter.filterOutAllExcept(camposArray);
        } else {
            filter = SimpleBeanPropertyFilter.serializeAll();
        }

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("filtroDinamico", filter);

        wrapper.setFilters(filters);

        return wrapper;
    }

}