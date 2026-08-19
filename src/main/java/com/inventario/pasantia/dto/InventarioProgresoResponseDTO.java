package com.inventario.pasantia.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioProgresoResponseDTO {
    private Long inventarioId;
    private List<InventarioProgresoItemDTO> items;
}
