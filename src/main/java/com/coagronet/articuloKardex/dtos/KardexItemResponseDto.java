package com.coagronet.articuloKardex.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record KardexItemResponseDto(
        Long id,
        String productoNombre,
        BigDecimal cantidad,
        BigDecimal precio,
        String lote,
        LocalDate fechaVencimiento,
        String estadoNombre
) {}
