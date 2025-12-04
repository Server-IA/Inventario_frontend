package com.coagronet.empresarol.mappers;

import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.dtos.EmpresaRolResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmpresaRolMapper {


    @Mapping(target = "empresaNombre", source = "empresa.nombre")
    @Mapping(target = "rolNombre", source = "rol.nombre")
    EmpresaRolResponseDTO toResponseDto(EmpresaRol empresaRol);
}
