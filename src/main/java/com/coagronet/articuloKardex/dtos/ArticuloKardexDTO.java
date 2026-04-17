package com.coagronet.articuloKardex.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticuloKardexDTO {

	private Long id;

	private BigDecimal cantidad;

	private BigDecimal precio;

	private LocalDate fechaVencimiento;

	private String identificadorProducto;

	@NotNull(message = "El kardex es obligatorio.")
	private Long kardexId;

	@NotNull(message = "La presentación de producto es obligatoria.")
	private Long presentacionProductoId;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;

	@Size(max = 255, message = "El lote no puede superar los 255 caracteres")
	private String lote;

	private String username;

	private String rol;

	private String ip;

	private String host;

	private LocalDateTime fechaHora;

}