package com.inventario.empresarol.dtos.responses;


import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class EmpresaRolResponseDTO {

    private Long id;

    private Long empresaId;

    private String empresaNombre;

    private String rolNombre;

    private String estadoNombre;

}
