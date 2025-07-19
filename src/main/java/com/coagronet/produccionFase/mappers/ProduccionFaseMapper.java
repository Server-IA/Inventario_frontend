package com.coagronet.produccionFase.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.produccionFase.ProduccionFase;
import com.coagronet.produccionFase.dtos.ProduccionFaseDTO;

@Mapper(componentModel = "spring")
public interface ProduccionFaseMapper {
    @Mapping(source = "variedad.id", target = "variedadId")
    @Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
    @Mapping(source = "fase.id", target = "faseId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    ProduccionFaseDTO toDto(ProduccionFase produccionFase);

    @Mapping(source = "variedadId", target = "variedad.id")
    @Mapping(source = "tipoProduccionId", target = "tipoProduccion.id")
    @Mapping(source = "faseId", target = "fase.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    ProduccionFase toEntity(ProduccionFaseDTO produccionFaseDTO);

    @Mapping(source = "variedad.id", target = "variedadId")
    @Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
    @Mapping(source = "fase.id", target = "faseId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    ProduccionFaseDTO toListDTO(ProduccionFase produccionFase);
    
}
