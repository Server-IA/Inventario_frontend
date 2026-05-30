package com.coagronet.pasantia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    private String identificador;
    private String nuevoIdentificador;
    private String nombre;
    private Long subseccionId;
    private Integer cantidadEsperada;
}
