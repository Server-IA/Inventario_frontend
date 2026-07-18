package com.coagronet.reports.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportePedidoJasperRowDTO {

	private Long pedidoId;
	private LocalDateTime fechaPedido;
	private String estado;
	private String empresa;
	private String sede;
	private String bloque;
	private String espacio;
	private String almacen;
	private String municipio;
	private String responsable;
	private String contacto;
	private String correo;
	private Integer itemIndice;
	private Long presentacionId;
	private String producto;
	private BigDecimal cantidad;
	private String unidad;
	private boolean tieneProducto;
	private BigDecimal totalPedido;
	private String advertencia;

}
