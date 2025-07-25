package com.coagronet.seccion.mapper;

import com.coagronet.seccion.Seccion;
import com.coagronet.seccion.dtos.SeccionDTO;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SeccionMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	@Mapping(source = "espacio.id", target = "espacioId")
	SeccionDTO toDTO(Seccion seccion);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(source = "espacioId", target = "espacio.id")
	Seccion toEntity(SeccionDTO seccionDTO);

	@Named("toListDTO")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(target = "empresaId", ignore = true)
	@Mapping(source = "espacio.id", target = "espacioId")
	SeccionDTO toListDTO(Seccion seccion);

	@IterableMapping(qualifiedByName = "toListDTO")
	List<SeccionDTO> toListDTO(List<Seccion> secciones);

}