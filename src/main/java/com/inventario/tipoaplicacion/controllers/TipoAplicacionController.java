package com.inventario.tipoaplicacion.controllers;

import java.util.List;
import java.util.Objects;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.tipoaplicacion.TipoAplicacion;
import com.inventario.tipoaplicacion.services.TipoAplicacionService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Controlador REST encargado de exponer los recursos de lectura para el
 * catálogo de {@link TipoAplicacion}.
 * <p>
 * Gestiona las peticiones HTTP bajo la ruta
 * <code>/api/v1/tipo-aplicaciones</code>, actuando como la capa de
 * presentación para las plataformas o entornos de despliegue soportados por el
 * sistema.
 * </p>
 * <p>
 * Implementa un mecanismo de proyección dinámica de datos mediante
 * {@link MappingJacksonValue}, permitiendo
 * a los clientes solicitar únicamente los atributos necesarios para su vista
 * (Pattern: Squiggly Filter).
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see TipoAplicacionService
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/tipo-aplicaciones")
public class TipoAplicacionController {

    private final TipoAplicacionService tipoAplicacionService;

    /**
     * Inicializa el controlador inyectando la dependencia de servicio requerida.
     * <p>
     * Se aplica inyección por constructor para asegurar la inmutabilidad de la
     * dependencia y facilitar
     * la testabilidad del componente.
     * </p>
     *
     * @param tipoAplicacionService componente de lógica de negocio para la entidad
     *                              TipoAplicacion.
     *                              Gestionado por el contenedor de Spring.
     */
    public TipoAplicacionController(TipoAplicacionService tipoAplicacionService) {
        this.tipoAplicacionService = tipoAplicacionService;
    }

    /**
     * Recupera el listado completo de tipos de aplicación aplicando filtros de
     * serialización dinámicos.
     * <p>
     * Este método consulta todos los registros disponibles a través del servicio y
     * los envuelve en un
     * contenedor {@link MappingJacksonValue}. Si el cliente especifica el parámetro
     * <code>campos</code>,
     * se configura un filtro al vuelo para excluir todas las propiedades excepto
     * las solicitadas.
     * </p>
     * <p>
     * Ejemplo de uso: <code>GET /api/v1/tipo-aplicaciones?campos=id,nombre</code>
     * retornará un JSON
     * conteniendo solo esas dos propiedades para cada elemento de la lista.
     * </p>
     *
     * @param campos cadena de texto opcional con los nombres de los atributos a
     *               incluir en la respuesta,
     *               separados por comas. Si es <code>null</code> o una cadena
     *               vacía, se retorna la estructura
     *               completa de la entidad.
     * @return un objeto {@link MappingJacksonValue} que contiene la lista de
     *         plataformas y la definición
     *         del filtro "filtroDinamico" a aplicar durante la serialización JSON.
     * @see SimpleBeanPropertyFilter#filterOutAllExcept(String...)
     */
    @GetMapping
    public MappingJacksonValue findAll(@RequestParam(required = false) String campos) {

        List<TipoAplicacion> lista = tipoAplicacionService.findAll();

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
