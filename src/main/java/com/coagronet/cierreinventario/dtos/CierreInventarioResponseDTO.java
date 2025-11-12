package com.coagronet.cierreinventario.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CierreInventarioResponseDTO {

    private Long id;

    private Long empresaId;

    private Long usuarioId;

    private Long almacenId;

    private Long anio;

    private Long mes;
}
