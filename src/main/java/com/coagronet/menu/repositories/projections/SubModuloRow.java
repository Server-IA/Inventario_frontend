package com.coagronet.menu.repositories.projections;

/**
 * Proyección de fila plana resultante de la consulta nativa del menú.
 * <p>
 * Contiene datos del subsistema (nombre, icono) y del módulo (id legible,
 * nombre, URL de navegación e icono). Se usa para mapear a DTOs de respuesta.
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public interface SubModuloRow {

	/** Nombre del subsistema al que pertenece el módulo. */
	String getSubNombre();

	/** Icono (nombre/clase) asociado al subsistema. Puede ser {@code null}. */
	String getSubIcon();

	/** Identificador legible del módulo (por ejemplo, clave funcional). */
	String getModNombreId();

	/** Nombre visible del módulo. */
	String getModNombre();

	/** URL relativa o ruta del front donde navega el módulo. */
	String getModUrl();

	/** Icono (nombre/clase) asociado al módulo. Puede ser {@code null}. */
	String getModIcon();

}
