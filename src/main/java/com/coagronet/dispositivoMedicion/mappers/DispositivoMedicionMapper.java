package com.coagronet.dispositivoMedicion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.dispositivoMedicion.DispositivoMedicion;
import com.coagronet.dispositivoMedicion.dtos.DispositivoMedicionDTO;

@Mapper(componentModel = "spring")
public interface DispositivoMedicionMapper {
    @Mapping(source = "subseccionDispositivo.id", target = "subseccionDispositivoId")
    @Mapping(source = "tipoMedicion.id", target = "tipoMedicionId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    DispositivoMedicionDTO toDto(DispositivoMedicion dispositivoMedicion);

    @Mapping(source = "subseccionDispositivoId", target = "subseccionDispositivo.id")
    @Mapping(source = "tipoMedicionId", target = "tipoMedicion.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    DispositivoMedicion toEntity(DispositivoMedicionDTO dispositivoMedicionDTO);

    @Mapping(source = "subseccionDispositivo.id", target = "subseccionDispositivoId")
    @Mapping(source = "tipoMedicion.id", target = "tipoMedicionId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    DispositivoMedicionDTO toListDto(DispositivoMedicion dispositivoMedicion);
}
