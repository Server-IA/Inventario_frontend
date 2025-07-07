package com.coagronet.estadoCategoria.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadoCategoriaDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @Size(max = 2048, message = "La descripción no puede tener más de 2048 caracteres")
    private String descripcion;
}
