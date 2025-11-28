package com.coagronet.usuariorol.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.coagronet.usuariorol.UsuarioRol;
import com.coagronet.usuariorol.dtos.UsuarioRolRequestDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolResponseDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioRolMapper {

    @Mapping(target = "usuarioId", source = "user.id")
    @Mapping(target = "usuarioEmail", source = "user.username")
    @Mapping(target = "empresaId", source = "empresa.id")
    @Mapping(target = "empresaNombre", source = "empresa.nombre")
    @Mapping(target = "rolId", source = "rol.id")
    @Mapping(target = "rolNombre", source = "rol.nombre")
    @Mapping(target = "estadoId", source = "estado.id")
    @Mapping(target = "estadoNombre", source = "estado.nombre")
    @Mapping(target = "createdBy", source = "createdBy.username")
    @Mapping(target = "updatedBy", source = "updatedBy.username")
    UsuarioRolResponseDTO toResponse(UsuarioRol entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "requestHost", ignore = true)
    @Mapping(target = "requestIp", ignore = true)
    UsuarioRol toEntity(UsuarioRolRequestDTO request);

    // ====== REQUEST -> ENTITY EXISTENTE (UPDATE REAL, NO PATCH) ======
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "id", ignore = true)
    // Relaciones se siguen manejando en el service (para validar con repos):
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "estado", ignore = true)
    // Auditoría nunca se toca desde el DTO:
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    // Estos se llenan con RequestUtils, no desde el cliente:
    @Mapping(target = "requestHost", ignore = true)
    @Mapping(target = "requestIp", ignore = true)
    void updateEntityFromRequest(UsuarioRolRequestDTO request, @MappingTarget UsuarioRol entity);
}