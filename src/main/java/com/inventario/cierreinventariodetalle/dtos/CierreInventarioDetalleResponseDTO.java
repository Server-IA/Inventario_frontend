package com.inventario.cierreinventariodetalle.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CierreInventarioDetalleResponseDTO {

    private Long id;

    private Long cierreInventarioId;

    private String productoPresentacionNombre;

    private BigDecimal stock;

    private LocalDate fechaCorte;

    private String empresaNombre;

    private String almacenNombre;


}
