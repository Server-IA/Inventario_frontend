package com.coagronet.pedido.estados;

import java.util.Arrays;

public enum PedidoEstadoEnum {

	ACTIVO(PedidoEstadoConst.ACTIVO, "pedido.estado.activo"),
	PENDIENTE_COTIZACION(PedidoEstadoConst.PENDIENTE_COTIZACION, "pedido.estado.pendiente-cotizacion"),
	CON_ORDEN_COMPRA(PedidoEstadoConst.CON_ORDEN_COMPRA, "pedido.estado.con-orden-compra"),
	ANULADO(PedidoEstadoConst.ANULADO, "pedido.estado.anulado"),
	COMPLETADO(PedidoEstadoConst.COMPLETADO, "pedido.estado.completado");

	public final String acronimo;

	public final String labelKey;

	PedidoEstadoEnum(String acronimo, String labelKey) {
		this.acronimo = acronimo;
		this.labelKey = labelKey;
	}

	public static PedidoEstadoEnum fromAcronimo(String acr) {
		return Arrays.stream(values())
			.filter(e -> e.acronimo.equalsIgnoreCase(acr))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("acronimo.not-found" + acr));
	}

}
