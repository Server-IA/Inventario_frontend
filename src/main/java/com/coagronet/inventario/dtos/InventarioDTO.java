package com.coagronet.inventario.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventarioDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @Size(max = 2048, message = "La descripción no puede tener más de 2048 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha y hora es obligatoria")
    private LocalDateTime fechaHora;

    @NotNull(message = "El tipo de inventario no puede ser nulo")
    private Long tipoInventarioId;

    private Long empresaId;

    @NotNull(message = "La subsección no puede ser nula")
    private Long subseccionId;

    @NotNull(message = "El estado no puede ser nulo")
    private Long estadoId;
}
