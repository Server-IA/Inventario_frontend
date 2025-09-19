package com.coagronet.pedido.dtos;

import java.time.LocalDateTime;

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
public class PedidoDTO {

	private Long id;

	private LocalDateTime fechaHora;

	@NotBlank(message = "{validation.descripcion.not-blank}")
	@Size(max = 2048, message = "{validation.descripcion.length}")
	private String descripcion;

	@NotNull(message = "{validation.almacen.not-null}")
	private Long almacenId;

	@NotNull(message = "{validation.produccion.not-null}")
	private Long produccionId;

	@NotNull(message = "{validation.estado.not-null}")
	private Long estadoId;

	private Long empresaId;

}
