package com.coagronet.cierreinventario.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CierreInventarioRequestDTO {

    @NotNull(message = "Almacen no puede ser nulo")
    private Long almacenId;

    @NotNull(message = "Año no puede ser nulo")
    private Long anio;

    @NotNull(message = "Mes no puede ser nulo")
    private Long mes;




}
