package com.coagronet.tipoaplicacion.enums;

/**
 * Enum que representa el tipo de aplicación a filtrar para el menú.
 * <p>
 * Se utiliza para traducir valores de entrada (cadena) a un identificador
 * interno que coincide con el almacenado en la base de datos.
 * </p>
 *
 * <ul>
 * <li>{@link #WEB} → id = 1</li>
 * <li>{@link #MOVIL} → id = 2</li>
 * </ul>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public enum TipoAplicacionEnum {

	WEB(1), MOVIL(2);

	private final int id;

	/**
	 * Crea el enum con su identificador interno.
	 * 
	 * @param id identificador persistido/esperado en BD
	 */
	TipoAplicacionEnum(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el identificador interno asociado al tipo.
	 * 
	 * @return id entero (p. ej., 1 para WEB, 2 para MOVIL)
	 */
	public int id() {
		return id;
	}

	/**
	 * Convierte una cadena a {@link TipoAplicacionEnum}, ignorando
	 * mayúsculas/minúsculas y espacios.
	 *
	 * @param raw texto a convertir; valores soportados: {@code "web"} |
	 *            {@code "movil"}
	 * @return enum correspondiente
	 * @throws IllegalArgumentException si el valor no es reconocido
	 */
	public static TipoAplicacionEnum from(String raw) {
		return switch (raw.trim().toLowerCase()) {
		case "web" -> WEB;
		case "movil" -> MOVIL;
		default -> throw new IllegalArgumentException("tipoAplicacion inválido. Use: web | movil");
		};
	}

}
