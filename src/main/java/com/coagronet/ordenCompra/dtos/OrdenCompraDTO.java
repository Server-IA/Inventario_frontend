package com.coagronet.ordenCompra.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrdenCompraDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private Long pedidoId;
    private Long proveedorId;
    private String descripcion;
    private Long estadoId;
    private Long empresaId;
}
