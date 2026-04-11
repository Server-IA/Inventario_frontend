package com.coagronet.kardex.dtos;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record KardexListDto(
    Long id,
    OffsetDateTime fechaHora,
    String almacenNombre,
    String tipoMovimientoNombre,
    String estadoNombre,
    String empresaNombre
) {}
