package com.inventario.cierreinventario.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CierreInventarioResponseDTO {

    private Long id;

    private String empresaNombre;

    private String usuarioNombre;

    private String almacenNombre;

    private LocalDate fechaCorte;

    private String descripcion;
}
