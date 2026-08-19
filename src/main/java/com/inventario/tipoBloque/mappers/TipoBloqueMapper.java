package com.inventario.tipoBloque.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.inventario.estado.Estado;
import com.inventario.tipoBloque.TipoBloque;
import com.inventario.tipoBloque.dtos.TipoBloqueDTO;

@Mapper(componentModel = "spring")
public interface TipoBloqueMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	TipoBloqueDTO toDTO(TipoBloque tipoBloque);

	@Mapping(target = "empresaId", ignore = true)
	@Mapping(target = "estadoId", ignore = true)
	@Mapping(target = "descripcion", ignore = true)
	TipoBloqueDTO toMinimalDTO(TipoBloque tipoBloque);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	TipoBloque toEntity(TipoBloqueDTO tipoBloqueDTO);

	@AfterMapping
	default void setEstadoAfterMapping(@MappingTarget TipoBloque tipoBloque, TipoBloqueDTO tipoBloqueDTO) {
		if (tipoBloque.getEstado() == null && tipoBloqueDTO.getEstadoId() != null) {
			Estado estado = new Estado();
			estado.setId(tipoBloqueDTO.getEstadoId());
			tipoBloque.setEstado(estado);
		}
	}

}
