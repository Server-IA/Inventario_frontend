package com.inventario.modulo.dtos;

import com.inventario.modulo.Modulo;

/**
 * Proyección inmutable (DTO) que resume la información esencial de un módulo para su visualización.
 * <p>
 * Se utiliza para desacoplar la entidad de base de datos de la respuesta de la API.
 * </p>
 *
 * @param id Identificador numérico del módulo.
 * @param nombre Nombre legible por el usuario.
 * @param url Ruta de acceso o endpoint asociado.
 * @param descripcion Detalle funcional del módulo.
 * @param icon Identificador o clase del icono visual.
 * @param estado Nombre del estado actual (ej. "Activo").
 * @param subSistema Nombre del subsistema al que pertenece.
 * @param tipoModulo Categorización del módulo.
 * @param tipoAplicacion Tipo de aplicación asociada.
 * @param nombreId Identificador de cadena único (slug).
 * @param requerido Indica si el módulo es de uso obligatorio para la empresa.
 */
public record ModuloSummaryResponse(Long id, String nombre, String url, String descripcion, String icon, String estado,
        String subSistema, String tipoModulo, String tipoAplicacion, String nombreId, Boolean requerido) {

    /**
     * Método de fábrica que convierte una entidad {@link Modulo} en este DTO.
     * <p>
     * Maneja la navegación segura de nulos para las relaciones (estado, subsistema, etc.), extrayendo solo los nombres
     * descriptivos necesarios para la vista.
     * </p>
     *
     * @param modulo La entidad fuente persistida.
     * @return Una nueva instancia del record con los datos aplanados.
     */
    public static ModuloSummaryResponse fromEntity(Modulo modulo) {
        return new ModuloSummaryResponse(modulo.getId(), modulo.getNombre(), modulo.getUrl(), modulo.getDescripcion(),
                modulo.getIcon(), modulo.getEstado() != null ? modulo.getEstado().getNombre() : null,
                modulo.getSubSistema() != null ? modulo.getSubSistema().getNombre() : null,
                modulo.getTipoModulo() != null ? modulo.getTipoModulo().getNombre() : null,
                modulo.getTipoAplicacion() != null ? modulo.getTipoAplicacion().getNombre() : null,
                modulo.getNombreId(), modulo.getRequerido());
    }

}
