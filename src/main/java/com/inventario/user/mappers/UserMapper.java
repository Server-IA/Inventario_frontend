package com.inventario.user.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.inventario.user.User;
import com.inventario.user.dtos.UserDTO;
import com.inventario.user.dtos.UserMinimalDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(source = "persona.id", target = "personaId")
	@Mapping(source = "usuarioEstado.id", target = "usuarioEstadoId")
	UserDTO toDto(User user);

	@Mapping(source = "persona.nombre", target = "nombre")
	UserMinimalDTO toMinimalDTO(User user);

	@Mapping(source = "personaId", target = "persona.id")
	@Mapping(source = "usuarioEstadoId", target = "usuarioEstado.id")
	@Mapping(target = "rolesAsignados", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	User toEntity(UserDTO userDTO);

	@Mapping(source = "personaId", target = "persona.id")
	@Mapping(source = "usuarioEstadoId", target = "usuarioEstado.id")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "rolesAsignados", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	void updateEntityFromDto(UserDTO userDTO, @MappingTarget User user);

}