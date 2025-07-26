package com.coagronet.programacion.dtos;

import java.sql.Timestamp;

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
public class ProgramacionDTO {
    
    private Long id;

    @Size(max = 2048, message = "La descripción no puede exceder los 2048 carácteres.")
    private String descripcion;

    @NotBlank(message = "El comando no puede estar vacío.")
    @Size(max = 255, message = "El comando no puede exceder los 255 carácteres.")
    private String comando;

    @NotNull(message = "La fecha y hora no puede ser nula.")
    private Timestamp fechaHora;

    @NotNull(message = "El dispositivo de la subsección no puede ser nulo.")
    private Long subseccionDispositivoId;

    @NotNull(message = "El estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;
}
