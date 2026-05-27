package com.coagronet.municipio.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MunicipioDTO {

	private Long id;

	@NotBlank(message = "{municipality.name.required}")
	@Size(max = 60, message = "{municipality.name.max-size}")
	private String nombre;

	@NotNull(message = "{municipality.department.required}")
	private Long departamentoId;

	@Positive(message = "{municipality.code.positive}")
	private Integer codigo;

	@Size(max = 3, message = "{municipality.acronym.size}")
	@Pattern(regexp = "^[A-Za-z]{1,3}$", message = "{municipality.acronym.only-letters}")
	private String acronimo;

	@NotNull(message = "{municipality.status.required}")
	private Long estadoId;

}
