package com.coagronet.pedido.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PedidoDTO {
    private Integer id;
    private LocalDateTime fechaHora;
    private Integer almacen;
    private Integer produccion;
    private String descripcion;
    private Integer estado;
}
