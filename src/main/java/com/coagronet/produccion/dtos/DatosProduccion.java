package com.coagronet.produccion.dtos;

import java.time.LocalDateTime;

import com.coagronet.produccion.Produccion;

public record DatosProduccion(Integer id, String nombre, Integer tipoProduccion, String descripcion,
        LocalDateTime fechaInicio, LocalDateTime fechaFinal, Integer espacio, Integer estado) {
    public DatosProduccion(Produccion produccion) {
        this(produccion.getId(), produccion.getNombre(), produccion.getTipoProduccion().getId(),
                produccion.getDescripcion(), produccion.getFechaInicio(), produccion.getFechaFinal(),
                produccion.getEspacio().getId(), produccion.getEstado().getId());
    }
}
