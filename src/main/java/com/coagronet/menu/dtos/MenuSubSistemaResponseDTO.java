package com.coagronet.menu.dtos;

import java.util.List;

import lombok.Builder;

@Builder
public record MenuSubSistemaResponseDTO(String nombre, String icono, List<MenuModuloResponseDTO> modulos) {

}
