package com.coagronet.inventario.gateway;

import lombok.Builder;
import lombok.Value;

public interface InventarioGateway {

	/**
	 * Valida si un pedido puede marcarse como COMPLETADO: 1) Todo recibido f�sicamente 2)
	 * Calidad OK 3) Asentado en inventario/Kardex
	 */
	Result validarRequisitosParaCompletar(Long pedidoId);

	@Value
	@Builder
	class Result {

		boolean ok;

		String motivoFallo; // null si ok == true

	}

}
