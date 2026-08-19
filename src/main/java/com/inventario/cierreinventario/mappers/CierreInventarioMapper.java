package com.inventario.cierreinventario.mappers;

import com.inventario.cierreinventario.CierreInventario;
import com.inventario.cierreinventario.dtos.CierreInventarioRequestDTO;
import com.inventario.cierreinventario.dtos.CierreInventarioResponseDTO;
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
