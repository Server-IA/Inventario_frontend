package com.inventario.auth.dto;

public record SwitchContextRequestDTO(Long empresaId, Long rolId, Boolean rememberAsDefault) {
}
