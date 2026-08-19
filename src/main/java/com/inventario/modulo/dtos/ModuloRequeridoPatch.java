package com.inventario.modulo.dtos;

import java.io.Serializable;

/**
 * DTO que encapsula los campos permitidos para la actualización parcial de un módulo.
 * <p>
 * Diseñado específicamente para operaciones PATCH, donde solo se transmite el subconjunto de datos que debe ser
 * modificado.
 * </p>
 *
 * @param requerido Nuevo valor booleano para la propiedad de obligatoriedad. Puede ser nulo (lo que implica "sin
 * cambios").
 */
public record ModuloRequeridoPatch(Boolean requerido) implements Serializable {
}
