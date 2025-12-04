package com.coagronet.empresarol.dtos;


import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmpresaRolResponseDTO {

    private Long id;

    private String empresaNombre;

    private String rolNombre;

    private String estado;


}
