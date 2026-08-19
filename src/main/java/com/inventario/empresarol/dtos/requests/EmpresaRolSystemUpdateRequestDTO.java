package com.inventario.empresarol.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaRolSystemUpdateRequestDTO {
    @NotNull
    private Long rolId;

    private Long estadoId;

    private Long empresaId;
}
