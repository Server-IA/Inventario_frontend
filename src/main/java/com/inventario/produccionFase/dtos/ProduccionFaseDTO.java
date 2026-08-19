package com.inventario.produccionFase.dtos;

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
public class ProduccionFaseDTO {
    private Long id;

    @NotNull(message = "El número de dias no puede ser nulo.")
    private Long dias;

    @Size(message = "La descripción no puede exceder los 2048 cáracteres.")
    private String descripcion;

    @NotNull(message = "La variedad no puede ser nula.")
    private Long variedadId;

    @NotNull(message = "El tipo de produccion no puede ser nulo.")
    private Long tipoProduccionId;

    @NotNull(message = "La fase no puede ser nula.")
    private Long faseId;

    @NotNull(message = "El estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;
}
