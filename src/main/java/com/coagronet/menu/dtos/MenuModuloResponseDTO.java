package com.coagronet.menu.dtos;

/**
 * DTO de un módulo dentro del menú.
 *
 * @param id     identificador legible del módulo (clave funcional para la UI)
 * @param nombre nombre visible del módulo
 * @param url    ruta/URL a la que navega el módulo en el frontend
 * @param icono  nombre/clase del icono asociado (puede ser {@code null})
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public record MenuModuloResponseDTO(String id, String nombre, String url, String icono) {

}
