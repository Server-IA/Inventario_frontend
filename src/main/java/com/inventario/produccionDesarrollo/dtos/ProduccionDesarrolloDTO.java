package com.inventario.produccionDesarrollo.dtos;

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
public class ProduccionDesarrolloDTO {

    private Long id;

    @Size(max = 2048, message = "La descripción no puede exceder los 2048 carácteres.")
    private String descripcion;

    @NotNull(message = "El valor no puede ser nulo.")
    private Integer valor;

    @NotNull(message = "La variedad no puede ser nula.")
    private Long variedadId;

    @NotNull(message = "La fase no puede ser nula.")
    private Long faseId;

    @NotNull(message = "El tipo de medición no puede ser nulo.")
    private Long tipoMedicionId;

    @NotNull(message = "El estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;
}
