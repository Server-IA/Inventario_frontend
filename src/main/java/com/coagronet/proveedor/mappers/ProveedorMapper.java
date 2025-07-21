package com.coagronet.proveedor.mappers;

import com.coagronet.proveedor.Proveedor;
import com.coagronet.proveedor.dtos.ProveedorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "tipoIdentificacion.id", target = "tipoIdentificacionId")
    ProveedorDTO toDto(Proveedor entity);



    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "tipoIdentificacionId", target = "tipoIdentificacion.id")
    Proveedor toEntity(ProveedorDTO dto);
}
