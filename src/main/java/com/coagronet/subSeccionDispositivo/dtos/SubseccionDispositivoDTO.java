package com.coagronet.subSeccionDispositivo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubseccionDispositivoDTO {
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío. ")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres. ")
    private String nombre;

    @Size(max = 2048, message = "La descripción no puede exceder los 2048 caracteres. ")
    private String descripcion;

    @NotNull(message = "La sub sección no puede ser nula.")
    private Long subseccionId;

    @NotNull(message = "El dispositivo no puede ser nulo. ")
    private Long tipoDispositivoId;

    @NotNull(message = "El estado no puede ser nulo. ")
    private Long estadoId;

    @NotNull(message = "La empresa no puede ser nula. ")
    private Long empresaId;
    
}
