package com.coagronet.variedad.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariedadDTO {
    
    private Long id;

    @NotBlank(message ="El nombre no puede ser nulo.")
    @Size(max = 100, message="El nombre no puede exceder los 100 caracteres.")
    private String nombre;

    @Size(max=255, message = "La descripción no puede superar los 255 caracteres.")
    private String descripcion;

    @NotNull(message = "El tipo de producción no puede ser nulo.")
    private Long tipoProduccionId;

    @NotNull(message = "El estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;

}
