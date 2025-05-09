package com.coagronet.tipoIdentificacion.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoIdentificacionDTO {

    private Long id;

    @Size(max = 255, message = "El nombre no debe superar los 255 caracteres.")
    private String nombre;

    @Size(max = 255, message = "La descripción no debe superar los 255 caracteres.")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio.")
    private Long estadoId;
    
}
