package com.coagronet.articuloKardex.dtos;

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

	private Double cantidad;

	private Double precio;

	private LocalDateTime fechaVencimiento;

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

}