package com.coagronet.pais.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaisDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 70, message = "El nombre no debe superar los 70 caracteres.")
    private String nombre;

    @NotNull(message = "El código no puede ser nulo")
    private Long codigo;

    @NotBlank(message = "El acronimo es obligatorio.")
    @Size(max = 3, message = "El acronimo no debe superar los 3 caracteres.")
    private String acronimo;

    @NotNull(message = "La empresa es obligatoria.")
    private Long empresaId;

    @NotNull(message = "El estado es obligatorio.")
    private Long estadoId;

}
