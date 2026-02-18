package com.coagronet.rolpermiso.dtos.request;

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
public class AsignarModulosMetodosRequest {

    /**
     * El `rolId` debe suministrarse en la URL como `@PathVariable`.
     */
    @NotEmpty
    private List<ModuloMetodoRequest> modulosMetodos;
}
