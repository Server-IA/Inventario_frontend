package com.coagronet.tipoMovimiento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoDTO;

@Mapper(componentModel = "spring")
public interface TipoMovimientoMapper {


    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "movimiento.id", target = "movimientoId")
    TipoMovimientoDTO toDto(TipoMovimiento tipoMovimiento);



    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "movimientoId", target = "movimiento.id")
    TipoMovimiento toEntity(TipoMovimientoDTO tipoMovimientoDTO);


}
