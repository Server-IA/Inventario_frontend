package com.coagronet.rolpermiso.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta después de asignar módulos y permisos a un rol.
 * Confirma visualmente al admin qué se asignó.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolPermisoAsignadoResponse {

    private Long rolId;
    private String rolNombre;
    
    /**
     * Cantidad total de permisos asignados
     */
    private Integer permisosAsignados;
    
    /**
     * Módulos que fueron asignados
     */
    private List<String> modulos;
    
    /**
     * Autoridades resultantes (ORDEN_COMPRA_READ_ALL, ORDEN_COMPRA_SEND, etc)
     */
    private List<String> autoridades;
}
