package com.coagronet.tipoDispositivo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.tipoDispositivo.TipoDispositivo;
import com.coagronet.tipoDispositivo.dtos.TipoDispositivoDTO;

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
