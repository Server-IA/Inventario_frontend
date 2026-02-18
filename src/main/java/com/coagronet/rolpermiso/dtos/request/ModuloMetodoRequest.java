package com.coagronet.rolpermiso.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuloMetodoRequest {

    @NotNull
    private Long moduloId;

    /**
     * Lista de métodos a asignar para este módulo. Ej: ["GET","POST"] o ["ALL"] o ["READ"]
     */
    @NotEmpty
    private List<String> metodos;
}
