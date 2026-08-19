package com.inventario.pasantia.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioCreateRequestDTO {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La fecha y hora son requeridas")
    private OffsetDateTime fechaHora;

    @NotNull(message = "El ID de la subsección es requerido")
    private Long subseccionId;

    @NotNull(message = "El usuario asignado es requerido")
    private Long usuarioAsignadoId;
}
