package com.inventario.tipomodulo.controllers;

import java.util.List;
import java.util.Objects;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.tipomodulo.TipoModulo;
import com.inventario.tipomodulo.services.TipoModuloService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Controlador REST encargado de exponer los recursos de lectura para el
 * catálogo de {@link TipoModulo}.
 * <p>
 * Gestiona las peticiones HTTP bajo la ruta <code>/api/v1/tipo-modulos</code>,
 * actuando como punto de entrada
 * para la consulta de las categorías funcionales del sistema. Implementa
 * patrones de proyección dinámica
 * de datos, permitiendo a los clientes REST solicitar únicamente los atributos
 * que requieren.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see TipoModuloService
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/tipo-modulos")
public class TipoModuloController {

    private final TipoModuloService tipoModuloService;

    /**
     * Inicializa el controlador inyectando la dependencia de servicio.
     * <p>
     * Se utiliza inyección por constructor para garantizar la disponibilidad del
     * servicio
     * {@link TipoModuloService} antes de procesar cualquier petición.
     * </p>
     *
     * @param tipoModuloService componente de lógica de negocio para la entidad
     *                          TipoModulo.
     */
    public TipoModuloController(TipoModuloService tipoModuloService) {
        this.tipoModuloService = tipoModuloService;
    }

    /**
     * Recupera el listado maestro de tipos de módulo aplicando filtros de
     * serialización dinámicos.
     * <p>
     * Consulta la totalidad de registros a través del servicio y envuelve la
     * respuesta en un objeto
     * {@link MappingJacksonValue}. Esto permite aplicar programáticamente el filtro
     * JSON definido en la
     * entidad (<code>@JsonFilter("filtroDinamico")</code>).
     * </p>
     * <p>
     * Si el parámetro <code>campos</code> está presente, la respuesta JSON
     * contendrá solo las propiedades
     * especificadas; de lo contrario, se serializará el objeto completo.
     * </p>
     *
     * @param campos cadena de texto opcional con los nombres de los atributos a
     *               incluir en la respuesta,
     *               separados por comas (ej. <code>id,nombre</code>). Si es
     *               <code>null</code> o vacío,
     *               se retornan todos los campos.
     * @return un contenedor {@link MappingJacksonValue} configurado con la lista de
     *         tipos y las reglas
     *         de filtrado activas.
     * @see SimpleBeanPropertyFilter#filterOutAllExcept(String...)
     */
    @GetMapping
    public MappingJacksonValue findAll(@RequestParam(required = false) String campos) {

        List<TipoModulo> lista = tipoModuloService.findAll();

        MappingJacksonValue wrapper = new MappingJacksonValue(Objects.requireNonNull(lista));

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
