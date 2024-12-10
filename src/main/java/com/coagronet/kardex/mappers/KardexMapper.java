package com.coagronet.kardex.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;

@Mapper(componentModel = "spring")
public interface KardexMapper {

    KardexMapper INSTANCE = Mappers.getMapper(KardexMapper.class);

    @Mapping(source = "almacen.id", target = "almacen")
    @Mapping(source = "produccion.id", target = "produccion")
    @Mapping(source = "tipoMovimiento.id", target = "tipoMovimiento")
    @Mapping(source = "estado.id", target = "estado")
    KardexDTO toDto(Kardex kardex);

    @Mapping(source = "almacen", target = "almacen.id")
    @Mapping(source = "produccion", target = "produccion.id")
    @Mapping(source = "tipoMovimiento", target = "tipoMovimiento.id")
    @Mapping(source = "estado", target = "estado.id")
    Kardex toEntity(KardexDTO kardexDTO);
}
