package com.inventario.productopresentacionstock.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPresentacionStockResponseDTO {

    private Long id;

    private BigDecimal stock;

    private LocalDateTime fechaHora;
    private Long productoPresentacionId;
    private String productoPresentacionNombre;

    private Long empresaId;
}
