package com.inventario.menu.dtos;

/**
 * Objeto de Transferencia de Datos (DTO) que define la estructura ligera de un módulo para su presentación en el menú.
 * <p>
 * Este registro inmutable encapsula los atributos necesarios para renderizar un elemento de navegación en la interfaz
 * de usuario, incluyendo su identidad, etiqueta visual y destino de enrutamiento.
 * </p>
 *
 * @param id El identificador único o clave del módulo en el sistema.
 * @param nombre La etiqueta de texto descriptiva que se mostrará al usuario en el menú.
 * @param url La ruta de navegación (URI) o endpoint al que redirige este módulo.
 * @param icono La cadena que representa el recurso gráfico asociado (ej. clase CSS, nombre de icono o URL de imagen).
 */
public record MenuModuloResponseDTO(String id, String nombre, String url, String icono) {

}
