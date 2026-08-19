package com.inventario.tipoDispositivo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.tipoDispositivo.TipoDispositivo;
import com.inventario.tipoDispositivo.dtos.TipoDispositivoDTO;

@Mapper(componentModel = "spring")
public interface TipoDispositivoMapper {
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    TipoDispositivoDTO toDto(TipoDispositivo tipoDispositivo);

    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    TipoDispositivo toEntity(TipoDispositivoDTO tipoDispositivoDTO);

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    TipoDispositivoDTO toListDTO(TipoDispositivo tipoDispositivo);
    
}
