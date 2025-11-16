package com.coagronet.cierreinventariodetalle.mappers;

import com.coagronet.cierreinventariodetalle.CierreInventarioDetalle;
import com.coagronet.cierreinventariodetalle.dtos.CierreInventarioDetalleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CierreInventarioDetalleMapper {

    @Mapping(target = "cierreInventarioId", source = "cierreInventario.id")
    @Mapping(target = "productoPresentacionNombre", source = "presentacionProducto.nombre")
    @Mapping(target = "empresaNombre", source = "empresa.nombre")
    @Mapping(target = "almacenNombre", source = "almacen.nombre")
    CierreInventarioDetalleResponseDTO toResponseDTO(CierreInventarioDetalle cierreInventarioDetalle);
}
