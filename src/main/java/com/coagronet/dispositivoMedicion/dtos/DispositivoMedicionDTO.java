package com.coagronet.dispositivoMedicion.dtos;

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
public class DispositivoMedicionDTO {
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String nombre;
    
    @Size(max=2048, message = "La descripción no puede excer los 2048 caracteres.")
    private String descripcion;

    @NotNull(message = "El valor no puede ser nulo.")
    private Integer valor;
    
    @NotNull(message = "La fecha no puede ser nula.")
    private Timestamp fechaHora;

    @NotNull(message = "El dispositivo no puede ser nulo.")
    private Long subseccionDispositivoId;

    @NotNull(message = "El tipo de medición no puede ser nulo.")
    private Long tipoMedicionId;

    @NotNull(message = "El estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;
}
