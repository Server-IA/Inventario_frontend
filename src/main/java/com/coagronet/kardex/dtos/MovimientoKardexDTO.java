package com.coagronet.kardex.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimientoKardexDTO(Long articuloKardexId, Long kardexId, OffsetDateTime fechaHora, String tipoMovimiento,
		String almacenOrigen, String productoIdentificador, BigDecimal cantidad, BigDecimal precioTotal,
		String estado) {
}
