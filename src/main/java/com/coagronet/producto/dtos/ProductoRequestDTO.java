package com.coagronet.producto.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoRequestDTO(
        @Size(max = 100, message = "{validation.nombre.length}") @NotNull(message = "{validation.nombre.not-null}") String nombre,
        @NotNull(message = "{validation.producto-categoria.not-null}") Long productoCategoriaId,
        @Size(max = 2048, message = "{validation.descripcion.length}") String descripcion,
        @NotNull(message = "{validation.estado.not-null}") Long estadoId,
        Long empresaId,
        @NotNull(message = "{validation.unidad.not-null}") Long unidadMinimaId,
        Integer cantidadMinima,
        Boolean esOrganico) {

}
