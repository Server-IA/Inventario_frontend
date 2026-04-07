package com.coagronet.empresarol.dtos.responses;


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

    public EmpresaRolResponseDTO(Long id, String empresaNombre, String rolNombre, String estadoNombre) {
        this.id = id;
        this.empresaNombre = empresaNombre;
        this.rolNombre = rolNombre;
        this.estadoNombre = estadoNombre;
    }

    public EmpresaRolResponseDTO(Long id, Long empresaId, String empresaNombre, String rolNombre, String estadoNombre) {
        this.id = id;
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
        this.rolNombre = rolNombre;
        this.estadoNombre = estadoNombre;
    }


}
