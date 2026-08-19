package com.inventario.auth.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterForCurrentEmpresaRequestDTO {

	@NotBlank
	@Email
	private String username;

	@NotNull
	private Long rolId;

	@NotBlank
	@Size(max = 100)
	private String nombre;

	@NotBlank
	@Size(max = 100)
	private String apellido;

	private String genero;

	private Long tipoDocumentoIdentidadId;

	private String codigoIdentificacion;

	private LocalDate fechaNacimiento;

	private Integer estrato;

	private String direccion;

	private String celular;

}
