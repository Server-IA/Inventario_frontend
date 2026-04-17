package com.coagronet.kardex.dtos;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Objeto de transferencia de datos que representa un elemento en la lista de movimientos de Kardex.")
public record KardexAdminListDTO(
        @Schema(description = "Identificador único del movimiento de Kardex", example = "1050") Long id,

        @Schema(description = "Fecha y hora exacta en la que se registró el movimiento", example = "2026-04-13T10:07:42-05:00") OffsetDateTime fechaHora,

        @Schema(description = "Nombre del almacén donde ocurrió el movimiento", example = "Almacén Central Campoalegre") String nombreAlmacen,

        @Schema(description = "Clasificación del tipo de movimiento", example = "ENTRADA_POR_COMPRA") String nombreTipoMovimiento,

        @Schema(description = "Código de la orden de producción", example = "PROD-2026-001") String nombreProduccion,

        @Schema(description = "Estado actual del movimiento en el sistema", example = "COMPLETADO") String nombreEstado,

        @Schema(description = "Nombre de la empresa. Nota: Por seguridad, este campo solo se incluye en la respuesta si el usuario que consulta tiene el rol de ADMINISTRADOR_SISTEMA, de lo contrario se omite.", example = "AgroNet Colombia", nullable = true) String nombreEmpresa,

        @Schema(description = "Nombre del cliente o proveedor asociado", example = "Distribuciones ABC") String nombreClienteProveedor,

        @Schema(description = "Nombre del almacén destino en caso de transferencias", example = "Almacén Secundario", nullable = true) String nombreAlmacenDestino) {
}
