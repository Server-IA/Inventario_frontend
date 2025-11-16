package com.coagronet.cierreinventario.mappers;

import com.coagronet.cierreinventario.CierreInventario;
import com.coagronet.cierreinventario.dtos.CierreInventarioRequestDTO;
import com.coagronet.cierreinventario.dtos.CierreInventarioResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CierreInventarioMapper {


    CierreInventario toEntity(CierreInventarioRequestDTO cierreInventarioRequestDTO);


    @Mapping(target = "empresaNombre", source = "empresa.nombre")
    @Mapping(target = "usuarioNombre", source = "usuario.username")
    @Mapping(target = "almacenNombre", source = "almacen.nombre")
    CierreInventarioResponseDTO toDTO(CierreInventario cierreInventario);

}
