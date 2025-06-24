package com.coagronet.inventario.mappers;


import com.coagronet.inventario.Inventario;
import com.coagronet.inventario.dtos.InventarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    @Mapping(source = "tipoInventario.id", target = "tipoInventarioId")
    @Mapping(source = "subseccion.id", target = "subseccionId")
    InventarioDTO toDTO(Inventario inventario);

    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "tipoInventarioId", target = "tipoInventario.id")
    @Mapping(source = "subseccionId", target = "subseccion.id")
    Inventario toEntity(InventarioDTO inventarioDTO);
}
