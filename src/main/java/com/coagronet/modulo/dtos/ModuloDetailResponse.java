package com.coagronet.modulo.dtos;

/**
 * Representa la estructura detallada de un {@link com.coagronet.subsistema.Modulo}, optimizada para operaciones de edición o auditoría.
 * <p>
 * A diferencia de las proyecciones de listado, este registro (<i>record</i>) expone los identificadores únicos (llaves foráneas)
 * de las entidades relacionadas (Estado, Subsistema, Tipos) en lugar de sus nombres descriptivos.
 * </p>
 * <p>
 * Este diseño facilita el enlace de datos (<i>data binding</i>) en formularios del frontend, permitiendo pre-seleccionar
 * los valores correctos en listas desplegables o componentes de selección.
 * </p>
 *
 * @param nombre           nombre comercial o funcional del módulo.
 * @param url              ruta relativa de navegación (endpoint) asociada al recurso.
 * @param descripcion      texto explicativo sobre el alcance y propósito del módulo.
 * @param icon             referencia a la clase CSS o recurso gráfico para la representación visual en menús.
 * @param estadoId         identificador numérico (ID) del {@link com.coagronet.estado.Estado} asignado.
 *                         Utilizado para inicializar selectores de estado.
 * @param subSistemaId     identificador numérico (ID) del {@link com.coagronet.subsistema.SubSistema} padre.
 * @param tipoModuloId     identificador numérico (ID) del {@link com.coagronet.tipomodulo.TipoModulo} que clasifica la naturaleza funcional del módulo.
 * @param tipoAplicacionId identificador numérico (ID) del {@link com.coagronet.tipoaplicacion.TipoAplicacion} que clasifica la plataforma de despliegue asociada.
 * @param roles            arreglo de cadenas con los códigos de seguridad necesarios para acceder al módulo.
 * @param nombreId         identificador técnico para uso en el DOM (Document Object Model) o pruebas automatizadas.
 * @param requerido        bandera que indica si el módulo es obligatorio para la operación del sistema.
 *                         Puede ser <code>true</code> o <code>false</code>.
 *
 * @author jujcgu
 * @version 1.0
 * @see com.coagronet.subsistema.Modulo
 * @see ModuloRequest
 * @since 2026
 */
public record ModuloDetailResponse(String nombre, String url, String descripcion, String icon, Long estadoId,
        Long subSistemaId, Long tipoModuloId, Long tipoAplicacionId, String[] roles, String nombreId,
        Boolean requerido) {

}
