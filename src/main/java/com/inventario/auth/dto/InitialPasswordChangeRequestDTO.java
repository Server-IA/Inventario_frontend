package com.inventario.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record InitialPasswordChangeRequestDTO(
        @NotBlank String nuevaClave,
        @NotBlank String confirmacionClave) {

}
