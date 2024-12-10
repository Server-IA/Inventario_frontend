package com.coagronet.kardexItem.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KardexItemDTO {
    private Integer id;
    private Integer kardex;
    private Integer productoPresentacion;
    private Double cantidad;
    private Double precio;
    private Integer estado;
}
