package com.coagronet.rol.dtos;

import java.time.OffsetDateTime;

public record RolResponseDTO(Long id, String nombre, String descripcion, Long estadoId, String estadoNombre,
                String createdBy, OffsetDateTime createdAt, String updatedBy, OffsetDateTime updatedAt,
                String deletedBy, OffsetDateTime deletedAt) {

}
