package com.coagronet.espacioOcupacion.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EspacioOcupacionDTO {
    private Long id;

    @NotNull(message = "El espacio no puede ser nulo")
    private Long espacioId;

    @NotNull(message = "La actividadOcupacion no puede ser nula")
    private Long actividadOcupacionId;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @NotNull(message = "El estado no puede ser nulo")
    private Long estadoId;
    private Long empresaId;
}
