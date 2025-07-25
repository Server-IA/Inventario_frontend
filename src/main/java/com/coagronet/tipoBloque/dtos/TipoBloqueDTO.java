package com.coagronet.tipoBloque.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoBloqueDTO {

	private Long id;

	@NotBlank
	private String nombre;

	private String descripcion;

	private Long estadoId;

	private Long empresaId;

}
