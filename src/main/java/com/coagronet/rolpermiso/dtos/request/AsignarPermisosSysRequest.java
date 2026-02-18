package com.coagronet.rolpermiso.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignarPermisosSysRequest {

    private Long empresaId;
    private Long rolId;
    private List<Long> permisosId;

}
