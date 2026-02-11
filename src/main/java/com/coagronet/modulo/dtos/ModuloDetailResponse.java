package com.coagronet.modulo.dtos;

public record ModuloDetailResponse(String nombre, String url, String descripcion, String icon, Long estadoId,
        Long subSistemaId, Long tipoModuloId, Long tipoAplicacionId, String[] roles, String nombreId,
        Boolean requerido) {

}
