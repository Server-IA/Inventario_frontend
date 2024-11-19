package com.coagronet.tipoMovimiento.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoMovimientoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer estado;
}
