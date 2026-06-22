package com.coagronet.pasantia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioProgresoItemDTO {
    @JsonProperty("producto_identificador")
    private String productoIdentificador;
    private Boolean encontrado;
    private String estado;
    private String observacion;
}
