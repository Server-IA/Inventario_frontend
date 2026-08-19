package com.inventario.pasantia.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioAsignadoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private OffsetDateTime fechaHora;
    private Long subSeccionId;
    private String subSeccionNombre;
    private String seccionNombre;
    private Short estadoId;
    private String estadoNombre;
    private Long usuarioAsignadoId;
}
