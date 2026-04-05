package com.coagronet.kardex.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ArticuloKardexListDto(Long id, BigDecimal cantidad, BigDecimal precio, BigDecimal precioTotal,
		OffsetDateTime fechaMovimiento, String tipoMovimiento, String productoNombre, String lote,
		String estadoMovimiento) {
}
