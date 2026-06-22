/*=============================================================================
 Nombre del archivo : KardexItemResponseDto.java
 Descripcion        : DTO de respuesta (record) para el detalle de Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-21 | 0.4.0   | JUAN JOSE CASTRO     | Adición de los atributos    |
 |            |         |                      | identificadorProducto,      |
 |            |         |                      | precioTotal y createdDate   |
 |            |         |                      | con anotaciones @Schema.    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.articuloKardex.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Representación de un ítem devuelto dentro del listado de un Kardex")
public record KardexItemResponseDto(
        @Schema(description = "Identificador único del ítem en el Kardex", example = "1024") Long id,

        @Schema(description = "Nombre completo del producto", example = "Abono Orgánico Compostado 50kg") String productoNombre,

        @Schema(description = "Identificador único asignado al producto", example = "123e4567-e89b-12d3-a456-426614174000") String identificadorProducto,

        @Schema(description = "Cantidad del producto afectada en este movimiento", example = "150.50") BigDecimal cantidad,

        @Schema(description = "Precio unitario del producto al momento del movimiento", example = "45000.00") BigDecimal precio,

        @Schema(description = "Precio total del movimiento (cantidad * precio)", example = "6750000.00") BigDecimal precioTotal,

        @Schema(description = "Código de lote asignado al producto", example = "LOTE-2026-04-A") String lote,

        @Schema(description = "Fecha de vencimiento del lote", example = "2027-12-31") LocalDate fechaVencimiento,

        @Schema(description = "Nombre del estado actual del ítem (ej. ACTIVO, INACTIVO)", example = "ACTIVO") String estadoNombre,

        @Schema(description = "Fecha de creación del registro") Instant createdDate) {
}
