package com.coagronet.reports.dtos;

public enum ReporteVencimientoProductoEstado {

	VENCIDO,

	PROXIMO_A_VENCER,

	TODOS;

	public static ReporteVencimientoProductoEstado normalize(ReporteVencimientoProductoEstado estado) {
		return estado == null ? TODOS : estado;
	}

}
