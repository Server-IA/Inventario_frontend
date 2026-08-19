package com.inventario.rolpermiso.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que agrupa todos los permisos de un módulo específico.
 * Usado para mostrar módulos disponibles y sus permisos al admin de empresa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuloPermisoResponse {

    private Long moduloId;
    private String moduloNombre;
    private String moduloUrl;
    private String moduloDescripcion;
    private String moduloIcon;

    /**
     * Lista de permisos agrupados en este módulo
     */
    private List<PermisoDTO> permisos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PermisoDTO {
        private Long id;
        private String nombre;           // "Listar todas las Ordenes de Compra"
        private String autoridad;        // "ORDEN_COMPRA_READ_ALL"
        private String metodo;           // "GET", "POST", "DELETE"
        private String uri;              // "/api/v1/orden-compra"
    }
}
