package com.coagronet.departamento.dtos;

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
public class DepartamentoDTO {

	private Long id;

	@NotBlank(message = "{department.name.required}")
	@Size(max = 70, message = "{department.name.max-size}")
	private String nombre;

	@NotNull(message = "{department.country.required}")
	private Long paisId;

	@NotNull(message = "{department.code.required}")
	@Positive(message = "{department.code.positive}")
	private Integer codigo;

	@NotBlank(message = "{department.acronym.required}")
	@Size(max = 3, message = "{department.acronym.size}")
	@Pattern(regexp = "^[A-Za-z]{1,3}$", message = "{department.acronym.only-letters}")
	private String acronimo;

	@NotNull(message = "{department.status.required}")
	private Long estadoId;

}
