package com.coagronet.produccionDesarrollo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.produccionDesarrollo.ProduccionDesarrollo;
import com.coagronet.produccionDesarrollo.dtos.ProduccionDesarrolloDTO;

@Mapper(componentModel = "spring")
public interface ProduccionDesarrolloMapper {
    @Mapping (source = "variedad.id", target = "variedadId")
    @Mapping (source = "fase.id", target = "faseId")
    @Mapping (source = "tipoMedicion.id", target = "tipoMedicionId")
    @Mapping (source = "estado.id", target = "estadoId")
    @Mapping (source = "empresa.id", target = "empresaId")
    ProduccionDesarrolloDTO toDto(ProduccionDesarrollo produccionDesarrollo);

    @Mapping (source = "variedadId", target = "variedad.id")
    @Mapping (source = "faseId", target = "fase.id")
    @Mapping (source =  "tipoMedicionId", target = "tipoMedicion.id")
    @Mapping (source = "estadoId", target = "estado.id")
    @Mapping (source = "empresaId", target = "empresa.id")
    ProduccionDesarrollo toEntity(ProduccionDesarrolloDTO produccionDesarrolloDTO);

    @Mapping (source = "variedad.id", target = "variedadId")
    @Mapping (source = "fase.id", target = "faseId")
    @Mapping (source = "tipoMedicion.id", target = "tipoMedicionId")
    @Mapping (source = "estado.id", target = "estadoId")
    @Mapping (source =  "empresa.id", target = "empresaId", ignore = true)
    ProduccionDesarrolloDTO toListDTO(ProduccionDesarrollo produccionDesarrollo);
    
}
