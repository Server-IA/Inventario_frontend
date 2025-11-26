package com.coagronet.rol.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RolRequestDTO(
        @Size(max = 100, message = "{validation.nombre.length}") @NotNull(message = "{validation.nombre.not-null}") String nombre,
        @Size(max = 2048, message = "{validation.descripcion.length}") String descripcion,
        @NotNull(message = "{validation.estado.not-null}") Long estadoId) {

}
