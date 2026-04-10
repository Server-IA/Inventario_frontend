package com.coagronet.kardex.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticuloUpdateResponseDTO(Long id, Long presentacionProductoId, BigDecimal cantidad, BigDecimal precio,
		Long responsableId, String lote, LocalDate fechaVencimiento) {
}