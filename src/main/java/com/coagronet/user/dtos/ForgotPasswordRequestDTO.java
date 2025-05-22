package com.coagronet.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
	@NotBlank(message = "El email es obligatorio")
	@Email(message = "Formato de email inválido")
	private String email;
}
