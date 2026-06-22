/*=============================================================================
 Nombre del archivo : RolMapper.java
 Descripcion        : Mapper de MapStruct para la conversión entre Rol y DTOs.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-22 | 0.4.0   | JUAN JOSE CASTRO     | Adición de anotaciones      |
 |            |         |                      | @Mapping en el método       |
 |            |         |                      | toEntity para ignorar el    |
 |            |         |                      | mapeo del id y los campos   |
 |            |         |                      | de auditoría (createdAt,    |
 |            |         |                      | updatedAt, deletedAt, etc). |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.rol.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.coagronet.rol.Rol;
import com.coagronet.rol.dtos.RolRequestDTO;
import com.coagronet.rol.dtos.RolResponseDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RolMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado.id", source = "estadoId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Rol toEntity(RolRequestDTO dto);

    @Mapping(target = "estadoId", source = "estado.id")
    @Mapping(target = "estadoNombre", source = "estado.nombre")
    @Mapping(target = "createdBy", source = "createdBy.username")
    @Mapping(target = "updatedBy", source = "updatedBy.username")
    RolResponseDTO toDTO(Rol rol);

}
