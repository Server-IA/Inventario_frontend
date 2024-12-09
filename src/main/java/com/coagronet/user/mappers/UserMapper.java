package com.coagronet.user.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.user.User;
import com.coagronet.user.dtos.UserDTO;
import com.coagronet.user.dtos.UserMinimalDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "persona.id", target = "persona")
    @Mapping(source = "usuarioEstado.id", target = "usuarioEstado")
    UserDTO toDto(User user);

    UserMinimalDTO toMinimalDTO(User user);

    @Mapping(source = "persona", target = "persona.id")
    @Mapping(source = "usuarioEstado", target = "usuarioEstado.id")
    User toEntity(UserDTO userDTO);
}
