package com.coagronet.facturacion.dtos;

import java.time.LocalDate;

import com.coagronet.validation.EstadoFacturacion;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacturacionDTO {

	private Long id;

	@Size(max = 10, message = "{validation.descripcion.length}")
	@NotBlank(message = "{validation.prefijo.not-blank}")
	private String prefijo;

	@NotNull(message = "{validation.numero-inicial.not-null}")
	private Long numeroInicial;

	@NotNull(message = "{validation.cantidad.not-null}")
	private Integer cantidad;

	@NotNull(message = "{validation.fecha-inicio.not-null}")
	private LocalDate fechaInicio;

	@NotNull(message = "{validation.fecha-final.not-null}")
	private LocalDate fechaFin;

	@NotNull(message = "{validation.estado.not-null}")
	@EstadoFacturacion(value = 4, message = "{validation.facturacion.estado.invalid-category}")
	private Long estadoId;

	private Long empresaId;

}
