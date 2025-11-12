package com.coagronet.cierreinventario.mappers;

import com.coagronet.cierreinventario.CierreInventario;
import com.coagronet.cierreinventario.dtos.CierreInventarioRequestDTO;
import com.coagronet.cierreinventario.dtos.CierreInventarioResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CierreInventarioMapper {


    CierreInventario toEntity(CierreInventarioRequestDTO cierreInventarioRequestDTO);

    CierreInventarioResponseDTO toDTO(CierreInventario cierreInventario);

}
