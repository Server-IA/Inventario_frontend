package com.coagronet.estadoCategoria.mappers;

import com.coagronet.estadoCategoria.EstadoCategoria;
import com.coagronet.estadoCategoria.dtos.EstadoCategoriaDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstadoCategoriaMapper {

	EstadoCategoriaDTO toDTO(EstadoCategoria estadoCategoria);

	EstadoCategoria toEntity(EstadoCategoriaDTO estadoCategoriaDTO);

}
