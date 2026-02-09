package com.coagronet.tipoaplicacion.controllers;

import java.util.List;
import java.util.Objects;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.tipoaplicacion.TipoAplicacion;
import com.coagronet.tipoaplicacion.services.TipoAplicacionService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@RestController
@RequestMapping("/api/v1/tipo-aplicaciones")
public class TipoAplicacionController {

    private final TipoAplicacionService tipoAplicacionService;

    public TipoAplicacionController(TipoAplicacionService tipoAplicacionService) {
        this.tipoAplicacionService = tipoAplicacionService;
    }

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
