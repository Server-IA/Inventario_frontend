package com.inventario.modulo.dtos;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Define el contrato de datos inmutable para la creación o actualización de un {@link com.inventario.subsistema.Modulo}.
 * <p>
 * Este registro (<i>record</i>) actúa como un Objeto de Transferencia de Datos (DTO) en la capa de presentación,
 * encapsulando los atributos necesarios para la persistencia. Incorpora anotaciones de <strong>Jakarta
 * Validation</strong> para garantizar la integridad de la información antes de ser procesada por la lógica de negocio.
 * </p>
 * <p>
 * Su diseño inmutable favorece la seguridad en entornos concurrentes y simplifica el transporte de datos entre el
 * cliente (Frontend) y el controlador REST.
 * </p>
 *
 * @param nombre cadena de caracteres obligatoria que representa el nombre comercial o funcional del módulo. Debe ser
 * única dentro del subsistema y no exceder los 100 caracteres.
 * @param url ruta relativa de navegación (endpoint) asociada al módulo. Campo obligatorio con longitud máxima de 100
 * caracteres.
 * @param descripcion texto explicativo opcional sobre el propósito del módulo. Permite hasta 2048 caracteres para
 * documentación detallada.
 * @param estadoId identificador numérico (llave foránea) del {@link com.inventario.estado.Estado}. Debe ser un valor
 * positivo y no nulo.
 * @param subSistemaId identificador numérico (llave foránea) del {@link com.inventario.subsistema.SubSistema} padre.
 * Debe ser un valor positivo y existir previamente en base de datos.
 * @param tipoModuloId identificador numérico (llave foránea) que clasifica el módulo (Reporte, Formulario, etc.).
 * Validado como número positivo obligatorio.
 * @param tipoAplicacionId identificador numérico (llave foránea) de la plataforma de despliegue. Requerido para la
 * unicidad compuesta del módulo.
 * @param nombreId identificador técnico para uso en pruebas automatizadas o referencias del DOM en el cliente.
 * @param requerido indicador booleano que determina si el módulo es indispensable para la operación del sistema. Si es
 * <code>null</code>, se asumirá el valor por defecto de la entidad.
 *
 * @author jujcgu
 * @version 2.0
 * @see com.inventario.subsistema.Modulo
 * @see Serializable
 * @since 2026
 */
public record ModuloRequest(@NotBlank @Size(max = 100) String nombre,

                @NotBlank @Size(max = 100) String url,

                @Size(max = 2048) String descripcion,

                @NotNull @Positive Long estadoId,

                @NotNull @Positive Long subSistemaId,

                @NotNull @Positive Long tipoModuloId,

                @NotNull @Positive Long tipoAplicacionId,

                @Size(max = 255) String nombreId,

                Boolean requerido

) implements Serializable {
}
