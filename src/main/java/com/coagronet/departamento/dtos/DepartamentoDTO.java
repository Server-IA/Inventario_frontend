package com.coagronet.departamento.dtos;

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
public class DepartamentoDTO {

    private Long id;

    @Size(max = 70, message = "El nombre no debe superar los 70 caracteres.")
    private String nombre;

    @NotNull(message = "El pais es obligatorio.")
    private Long paisId;

    @NotBlank(message = "El codigo es obligatorio.")
    private Integer codigo;

    @NotBlank(message = "El acronimo es obligatorio.")
    private String acronimo;

    @NotNull(message = "La empresa es obligatoria.")
    private Long empresaId;

    @NotNull(message = "El estado es obligatorio.")
    private Long estadoId;

}
