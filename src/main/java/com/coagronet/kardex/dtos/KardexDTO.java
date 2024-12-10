package com.coagronet.kardex.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KardexDTO {
    private Integer id;
    private LocalDateTime fechaHora;
    private Integer almacen;
    private Integer produccion;
    private Integer tipoMovimiento;
    private String descripcion;
    private Integer estado;
}
