package com.inventario.metricas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaTotalResponseDTO {
    private Long empresaId;
    private List<EntidadTotalDTO> totales;
    private List<String> entidadesInvalidas;
}
