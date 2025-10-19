package com.coagronet.metricas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntidadTotalDTO {
    private String entidad;
    private Long total;
}
