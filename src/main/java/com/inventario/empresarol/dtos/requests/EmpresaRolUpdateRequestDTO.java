package com.inventario.empresarol.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaRolUpdateRequestDTO {

    private Long rolId;

    private Long estadoId;
}