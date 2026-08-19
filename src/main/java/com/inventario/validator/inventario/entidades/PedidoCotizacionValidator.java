package com.inventario.validator.inventario.entidades;

import org.springframework.stereotype.Component;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.pedidocotizacion.PedidoCotizacion;
import com.inventario.pedidocotizacion.repositories.PedidoCotizacionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoCotizacionValidator {

	private final PedidoCotizacionRepository pedidoCotizacionRepository;

	public PedidoCotizacion validarPedidoCotizacion(Long pedidoId, Long empresaId) {
		return pedidoCotizacionRepository.findByIdAndPedidoEmpresaId(pedidoId, empresaId)
			.orElseThrow(() -> new NotFoundException("pedido-cotizacion.not-found", pedidoId));
	}

}
