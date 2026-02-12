package com.coagronet.menu.dtos;

import java.util.List;

import lombok.Builder;

/**
 * Objeto de Transferencia de Datos (DTO) que representa un subsistema y su colección de módulos asociados en el menú.
 * <p>
 * Se utiliza para estructurar la respuesta jerárquica del menú hacia el cliente.
 * </p>
 *
 * @param nombre El nombre descriptivo del subsistema.
 * @param icono La referencia o clase CSS del icono visual del subsistema.
 * @param modulos La lista de módulos {@link MenuModuloResponseDTO} contenidos en este subsistema.
 */
@Builder
public record MenuSubSistemaResponseDTO(String nombre, String icono, List<MenuModuloResponseDTO> modulos) {

}
