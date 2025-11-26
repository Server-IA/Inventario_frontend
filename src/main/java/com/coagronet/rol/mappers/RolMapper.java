package com.coagronet.rol.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.rol.Rol;
import com.coagronet.rol.dtos.RolRequestDTO;
import com.coagronet.rol.dtos.RolResponseDTO;

@Mapper(componentModel = "spring")
public interface RolMapper {

    @Mapping(target = "estado.id", source = "estadoId")
    Rol toEntity(RolRequestDTO dto);

    @Mapping(target = "estadoId", source = "estado.id")
    @Mapping(target = "estadoNombre", source = "estado.nombre")
    @Mapping(target = "createdBy", source = "createdBy.username")
    @Mapping(target = "updatedBy", source = "updatedBy.username")
    @Mapping(target = "deletedBy", source = "deletedBy.username")
    RolResponseDTO toDTO(Rol rol);

}
