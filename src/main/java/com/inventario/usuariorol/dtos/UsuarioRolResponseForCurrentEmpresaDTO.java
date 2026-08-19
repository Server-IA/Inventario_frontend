/*=============================================================================
 Nombre del archivo : UsuarioRolResponseForCurrentEmpresaDTO.java
 Descripcion        : DTO de respuesta para la información de UsuarioRol filtrada
                      por la empresa actual.
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

package com.inventario.usuariorol.dtos;

import java.time.OffsetDateTime;
import java.time.Instant;

public record UsuarioRolResponseForCurrentEmpresaDTO(
        Long id,
        Long usuarioId,
        String usuarioEmail,
        String personaNombreCompleto,
        Long rolId,
        String rolNombre,
        Long estadoId,
        String estadoNombre,
        OffsetDateTime iniciaContratoEn,
        OffsetDateTime finalizaContratoEn,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy) {

}
