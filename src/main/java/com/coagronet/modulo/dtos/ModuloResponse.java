package com.coagronet.modulo.dtos;

/**
 * Representa la proyección de salida de datos para un {@link com.coagronet.subsistema.Modulo}.
 * <p>
 * Este registro (<i>record</i>) actúa como un Objeto de Transferencia de Datos (DTO) de lectura, diseñado para entregar
 * información al cliente (Frontend) de forma optimizada. A diferencia del objeto de petición, esta estructura "aplana"
 * las relaciones de base de datos, entregando los nombres descriptivos de las entidades relacionadas (Estado,
 * Subsistema, Tipos) en lugar de sus identificadores numéricos, facilitando así su visualización directa en la interfaz
 * de usuario.
 * </p>
 *
 * @param id identificador único (llave primaria) del módulo en la base de datos.
 * @param nombre nombre comercial o funcional del módulo.
 * @param url ruta relativa de navegación (endpoint) configurada para el acceso desde el cliente.
 * @param descripcion detalle extendido del propósito del módulo. Puede ser <code>null</code> si no se definió.
 * @param icon identificador de la clase CSS o recurso gráfico asociado para la representación visual.
 * @param estado nombre descriptivo del estado operativo actual (ej. "Activo", "Inactivo"). Proyección de lectura de la
 * entidad {@link com.coagronet.estado.Estado}.
 * @param subSistema nombre del subsistema padre al que pertenece este módulo. Proyección de lectura de la entidad
 * {@link com.coagronet.subsistema.SubSistema}.
 * @param tipoModulo categoría funcional del módulo (ej. "Reporte", "Formulario"). Proyección de lectura de la entidad
 * {@link com.coagronet.tipomodulo.TipoModulo}.
 * @param tipoAplicacion nombre de la plataforma o entorno de despliegue (ej. "Web", "Móvil"). Proyección de lectura de
 * la entidad {@link com.coagronet.tipoaplicacion.TipoAplicacion}.
 * @param roles arreglo de cadenas con los códigos de los roles autorizados para acceder al recurso.
 * @param nombreId identificador técnico utilizado para referencias en el DOM o pruebas automatizadas.
 * @param requerido indica si el módulo es indispensable para la operación del sistema (<code>true</code>) o si es
 * opcional (<code>false</code>).
 *
 * @author jujcgu
 * @version 1.0
 * @see com.coagronet.subsistema.Modulo
 * @see ModuloRequest
 * @since 2026
 */
public record ModuloResponse(Long id,

                String nombre,

                String url,

                String descripcion,

                String icon,

                String estado,

                String subSistema,

                String tipoModulo,

                String tipoAplicacion,

                String[] roles,

                String nombreId,

                Boolean requerido) {

}
