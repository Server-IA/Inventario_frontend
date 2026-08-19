/*=============================================================================
 Nombre del archivo : EmpresaRolSelectDTO.java
 Descripcion        : DTO de respuesta para representar la información básica
                      (id y nombre) de los roles en listas de selección.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-27 | 0.4.0   | JUAN JOSE CASTRO     | Creación del DTO (record)   |
 |            |         |                      | para retornar los datos     |
 |            |         |                      | esenciales de los roles     |
 |            |         |                      | requeridos en los menús de  |
 |            |         |                      | selección.                  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.inventario.empresarol.dtos.responses;

public record EmpresaRolSelectDTO(Long id, String nombre) {
}
