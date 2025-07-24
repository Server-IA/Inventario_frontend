package com.coagronet.productoLocalizacion.mappers;

import com.coagronet.productoLocalizacion.ProductoLocalizacion;
import com.coagronet.productoLocalizacion.dtos.ProductoLocalizacionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ProductoLocalizacionMapper {

	@Mapping(source = "empresa.id", target = "empresaId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "subseccion.id", target = "subseccionId")
	@Mapping(source = "articuloKardex.id", target = "articuloKardexId")
	ProductoLocalizacionDTO toDto(ProductoLocalizacion productoLocalizacion);

	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "subseccionId", target = "subseccion.id")
	@Mapping(source = "articuloKardexId", target = "articuloKardex.id")
	ProductoLocalizacion toEntity(ProductoLocalizacionDTO productoLocalizacionDTO);

}
