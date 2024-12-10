package com.coagronet.kardexItem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.kardexItem.KardexItem;
import com.coagronet.kardexItem.dtos.KardexItemDTO;

@Mapper(componentModel = "spring")
public interface KardexItemMapper {

    KardexItemMapper INSTANCE = Mappers.getMapper(KardexItemMapper.class);

    @Mapping(source = "kardex.id", target = "kardex")
    @Mapping(source = "productoPresentacion.id", target = "productoPresentacion")
    @Mapping(source = "estado.id", target = "estado")
    KardexItemDTO toDto(KardexItem kardexItem);

    @Mapping(source = "kardex", target = "kardex.id")
    @Mapping(source = "productoPresentacion", target = "productoPresentacion.id")
    @Mapping(source = "estado", target = "estado.id")
    KardexItem toEntity(KardexItemDTO kardexItemDTO);
}
