package com.inventario.estadoCategoria.mappers;

import com.inventario.estadoCategoria.EstadoCategoria;
import com.inventario.estadoCategoria.dtos.EstadoCategoriaDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstadoCategoriaMapper {

	EstadoCategoriaDTO toDTO(EstadoCategoria estadoCategoria);

	EstadoCategoria toEntity(EstadoCategoriaDTO estadoCategoriaDTO);

}
