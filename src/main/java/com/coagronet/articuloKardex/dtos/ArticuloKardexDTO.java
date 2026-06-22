/*=============================================================================
 Nombre del archivo : ArticuloKardexDTO.java
 Descripcion        : Objeto de transferencia de datos (DTO) para ArticuloKardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-21 | 0.4.0   | JUAN JOSE CASTRO     | Reemplazo de LocalDateTime  |
 |            |         |                      | por Instant. Adición de la  |
 |            |         |                      | anotación @Builder. Nuevas  |
 |            |         |                      | validaciones para cantidad  |
 |            |         |                      | y precio. Creación de los   |
 |            |         |                      | campos precioTotal y        |
 |            |         |                      | responsableId.              |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.articuloKardex.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticuloKardexDTO {

	private Long id;

	@NotNull(message = "La cantidad es obligatoria.")
	@Positive(message = "La cantidad debe ser mayor a cero.")
	private BigDecimal cantidad;

	@NotNull(message = "El precio es obligatorio.")
	@DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
	private BigDecimal precio;

	private BigDecimal precioTotal;

	private LocalDate fechaVencimiento;

	private String identificadorProducto;

	@NotNull(message = "El kardex es obligatorio.")
	private Long kardexId;

	@NotNull(message = "La presentación de producto es obligatoria.")
	private Long presentacionProductoId;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;

	private Long responsableId;

	@Size(max = 255, message = "El lote no puede superar los 255 caracteres.")
	private String lote;

	// --- Campos de respuesta / Auditoría ---

	private String username;

	private String rol;

	private String ip;

	private String host;

	private Instant createdDate;
}