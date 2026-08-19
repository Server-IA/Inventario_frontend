package com.inventario.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {

	@NotBlank
	@Email
	private String username;

	@NotBlank
	private String password;

}
