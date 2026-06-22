/*=============================================================================
 Nombre del archivo : RolResponseDTO.java
 Descripcion        : DTO de respuesta para la información de un Rol.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-22 | 0.4.0   | JUAN JOSE CASTRO     | Reemplazo del tipo de dato  |
 |            |         |                      | OffsetDateTime por Instant  |
 |            |         |                      | en los atributos de         |
 |            |         |                      | auditoría createdAt y       |
 |            |         |                      | updatedAt.                  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.rol.dtos;

import java.time.Instant;

public record RolResponseDTO(Long id, String nombre, String descripcion, Long estadoId, String estadoNombre,
                String createdBy, Instant createdAt, String updatedBy, Instant updatedAt) {

}
