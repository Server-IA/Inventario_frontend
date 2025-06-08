package com.coagronet.user.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.user.User;
import com.coagronet.user.dtos.UserDTO;
import com.coagronet.user.dtos.UserMinimalDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(source = "persona.id", target = "personaId")
    @Mapping(source = "usuarioEstado.id", target = "usuarioEstadoId")
    UserDTO toDto(User user);

    UserMinimalDTO toMinimalDTO(User user);

    @Mapping(source = "personaId", target = "persona.id")
    @Mapping(source = "usuarioEstadoId", target = "usuarioEstado.id")
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserDTO userDTO);
}
