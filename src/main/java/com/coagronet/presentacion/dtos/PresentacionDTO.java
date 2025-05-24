package com.coagronet.presentacion.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresentacionDTO {

    private Long id;

    @NotBlank
    @Size(max = 255, message = "El nombre no debe superar los 255 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion no debe superar los 500 caracteres")
    private String descripcion;

    @NotNull
    private Long estadoId;


    private Long empresaId;
}
