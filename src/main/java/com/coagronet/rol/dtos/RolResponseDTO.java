package com.coagronet.rol.dtos;

import java.time.Instant;

public record RolResponseDTO(Long id, String nombre, String descripcion, Long estadoId, String estadoNombre,
        String createdBy, Instant createdAt, String updatedBy, Instant updatedAt) {

}
