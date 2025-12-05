package com.coagronet.empresarol.dtos.responses;


import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmpresaRolResponseDTO {

    private Long id;

    private String empresaNombre;

    private String rolNombre;

    private String estadoNombre;


}
