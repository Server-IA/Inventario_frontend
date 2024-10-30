package com.coagronet.produccion.dtos;

import com.coagronet.produccion.Produccion;

public record DatosListadoCortoProduccion(Integer id, String nombre) {
    public DatosListadoCortoProduccion(Produccion produccion) {
        this(produccion.getId(), produccion.getNombre());
    }
}
