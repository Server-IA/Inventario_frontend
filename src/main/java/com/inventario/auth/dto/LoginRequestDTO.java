package com.inventario.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

	@NotBlank(message = "El nombre de usuario es obligatorio")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	private String password;

}
