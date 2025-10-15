package com.coagronet.pedidocotizacion.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear o actualizar una {@code PedidoCotizacion}.
 *
 * <p>
 * Contiene los datos editables por el cliente. Las asociaciones con
 * {@code Pedido}, {@code Proveedor} y {@code Estado} se expresan por sus
 * identificadores.
 * </p>
 *
 * <h2>Validaciones</h2>
 * <ul>
 * <li>{@code descripcion}: longitud máxima 2048 caracteres.</li>
 * <li>{@code archivo}: longitud máxima 2048 caracteres.</li>
 * <li>{@code pedidoId}: obligatorio (no nulo).</li>
 * <li>{@code proveedorId}: obligatorio (no nulo).</li>
 * <li>{@code estadoId}: opcional; si se omite puede asignarse un estado por
 * defecto en la capa de servicio.</li>
 * </ul>
 *
 * <p>
 * Este DTO no incluye el campo {@code id} porque dicho valor se genera en el
 * backend.
 * </p>
 *
 * @param descripcion descripción libre de la cotización
 * @param archivo     referencia a archivo asociado (ruta/URL/nombre)
 * @param pedidoId    identificador del pedido al que pertenece la cotización
 * @param proveedorId identificador del proveedor al que se solicita la
 *                    cotización
 * @param estadoId    identificador del estado general de la cotización
 *                    (opcional)
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public record PedidoCotizacionRequestDTO(
		@Size(max = 2048, message = "{validation.descripcion.length}") String descripcion,

		@Size(max = 2048, message = "{validation.archivo.length}") String archivo,

		@NotNull(message = "{validation.pedido.not-null}") Long pedidoId,

		@NotNull(message = "{validation.proveedor.not-null}") Long proveedorId,

		Long estadoId

) {

}
