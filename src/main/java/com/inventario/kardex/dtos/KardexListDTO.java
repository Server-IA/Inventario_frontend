/*=============================================================================
 Nombre del archivo : KardexListDTO.java
 Descripcion        : DTO de respuesta para el listado general de Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-22 | 0.4.0   | JUAN JOSE CASTRO     | Adición de los atributos    |
 |            |         |                      | username y segFechaHora con |
 |            |         |                      | sus respectivas anotaciones |
 |            |         |                      | @Schema para reflejar la    |
 |            |         |                      | información de auditoría.   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.inventario.kardex.dtos;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Objeto de transferencia de datos que representa un elemento en la lista de movimientos de Kardex.")
public record KardexListDTO(
        @Schema(description = "Identificador único del movimiento de Kardex", example = "1050") Long id,

        @Schema(description = "Fecha y hora exacta en la que se registró el movimiento", example = "2026-04-13T10:07:42-05:00") OffsetDateTime fechaHora,

        @Schema(description = "Nombre del almacén donde ocurrió el movimiento", example = "Almacén Central Campoalegre") String nombreAlmacen,

        @Schema(description = "Clasificación del tipo de movimiento", example = "ENTRADA_POR_COMPRA") String nombreTipoMovimiento,

        @Schema(description = "Código de la orden de producción", example = "PROD-2026-001") String nombreProduccion,

        @Schema(description = "Estado actual del movimiento en el sistema", example = "COMPLETADO") String nombreEstado,

        @Schema(description = "Nombre del cliente o proveedor asociado", example = "Distribuciones ABC") String nombreClienteProveedor,

        @Schema(description = "Nombre del almacén destino en caso de transferencias", example = "Almacén Secundario", nullable = true) String nombreAlmacenDestino,

        @Schema(description = "Usuario que registró la auditoría", example = "admin@agro.com", nullable = true) String username,

        @Schema(description = "Fecha y hora de auditoría", example = "2026-06-21T10:07:42Z", nullable = true) java.time.Instant segFechaHora) {
}
