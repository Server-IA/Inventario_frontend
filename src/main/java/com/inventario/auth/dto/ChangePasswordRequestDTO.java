package com.inventario.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {

	@NotBlank(message = "La contraseña antigua es obligatoria")
	private String oldPassword;

	@NotBlank(message = "La nueva contraseña es obligatoria")
	private String newPassword;

}
