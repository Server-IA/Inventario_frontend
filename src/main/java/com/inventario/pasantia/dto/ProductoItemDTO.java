package com.inventario.pasantia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoItemDTO {
    
    private String producto;
    
    @JsonProperty("producto_identificador")
    private String productoIdentificador;
    
    private Integer cantidad;
}
