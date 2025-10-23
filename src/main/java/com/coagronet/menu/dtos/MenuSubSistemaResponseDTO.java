package com.coagronet.menu.dtos;

import java.util.List;

import lombok.Builder;

/**
 * DTO que representa un subsistema del menú (grupo) y sus módulos.
 *
 * @param nombre  nombre del subsistema
 * @param icono   icono del subsistema (puede ser {@code null})
 * @param modulos lista de módulos pertenecientes a este subsistema
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@Builder
public record MenuSubSistemaResponseDTO(String nombre, String icono, List<MenuModuloResponseDTO> modulos) {

}
