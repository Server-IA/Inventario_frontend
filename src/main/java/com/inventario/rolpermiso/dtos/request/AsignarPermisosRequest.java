package com.inventario.rolpermiso.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignarPermisosRequest {

    private List<Long> permisosId;

}
