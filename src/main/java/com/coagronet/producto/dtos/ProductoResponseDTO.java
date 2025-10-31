package com.coagronet.producto.dtos;

public record ProductoResponseDTO(Long id, String nombre, Long productoCategoriaId, String productoCategoriaNombre,
                String descripcion, Long estadoId, String estadoNombre, Long unidadMinimaId, String unidadMinimaNombre,
                Integer cantidadMinima, Boolean esOrganico) {

}
