package com.coagronet.kardex.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record KardexUpdateResponseDTO(Long id, Long tipoMovimientoId, Long almacenId, Long almacenDestinoId,
		Long ordenCompraId, Long pedidoId, Long produccionId, Long clienteProveedorId, String descripcion,
		List<ArticuloUpdateResponseDTO> items) {
}
