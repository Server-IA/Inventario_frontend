package com.coagronet.variedad.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.variedad.Variedad;
import com.coagronet.variedad.dtos.VariedadDTO;

@Mapper(componentModel = "spring")
public interface VariedadMapper {

    @Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
    @Mapping(source = "estado.id", target="estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    VariedadDTO toDto(Variedad variedad);

    @Mapping(source = "tipoProduccionId", target = "tipoProduccion.id")
    @Mapping(source = "estadoId", target="estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Variedad toEntity(VariedadDTO variedadDTO);

    @Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
    @Mapping(source = "estado.id", target="estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    VariedadDTO toListDTO(Variedad variedad);
    
}
