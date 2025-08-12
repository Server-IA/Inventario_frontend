package com.coagronet.modelo.dtos;

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
public class ModeloDTO {
    
    private Long id;

    @NotBlank(message = "El nombre  no puede estar vaćio.")
    @Size(max = 100, message = "El nombre no puee exceder los 100 caráteres.")
    private String nombre;

    @Size(max = 2048, message = "La descripción no puede exceder los 2048 carácteres.")
    private String descripcion;

    @NotBlank(message = "La url no puede estar vacía.")
    @Size(max = 1024, message = "La url no puede exceder los 1024 carácteres.")
    private String url;

    @NotNull(message = "La fecha y hora de inicio no pueden ser nulas.")
    private Timestamp fechaHoraInicio;

    @NotNull(message = "La fecha y hora de fin no pueden ser nulas.")
    private Timestamp fechaHoraFin;

    @NotNull(message = "La fecha y hora de ceración no pueden ser nulas.")
    private Timestamp fechaHoraCreacion;

    @NotNull(message = "El producto no puede ser nulo.")
    private Long productoId;

    @NotNull(message = "El tipo de modelo no puede ser nulo.")
    private Long tipoModeloId;

    @NotNull(message = "El estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;
}
