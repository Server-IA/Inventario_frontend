package com.coagronet.inventario.gateway;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.articuloPedido.repositories.ArticuloPedidoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventarioGatewayImpl implements InventarioGateway {

	private static final double EPS = 1e-6; // tolerancia num?rica

	private final ArticuloPedidoRepository articuloPedidoRepository;

	private final ArticuloKardexRepository articuloKardexRepository;

	@Override
	@Transactional(readOnly = true)
	public Result validarRequisitosParaCompletar(Long pedidoId) {
		if (!validarTodoRecibido(pedidoId)) {
			return Result.builder()
				.ok(false)
				.motivoFallo("Existen ?tems pendientes por recibir (comparativo Pedido vs Kardex ENTRADA).")
				.build();
		}
		if (!validarMovimientosKardex(pedidoId)) {
			return Result.builder()
				.ok(false)
				.motivoFallo("Faltan asientos de inventario (Kardex ENTRADA) para el pedido.")
				.build();
		}
		return Result.builder().ok(true).build();
	}

	boolean validarTodoRecibido(Long pedidoId) {
		var pedidas = articuloPedidoRepository.sumCantidadesPedidasGroupByPresentacion(pedidoId)
			.stream()
			.collect(Collectors.toMap(ArticuloPedidoRepository.RowCantidad::getPresentacionId,
					r -> safe(r.getCantidad())));

		if (pedidas.isEmpty())
			return false; // sin ?tems no se completa

		var entradas = articuloKardexRepository
			.sumCantidadesKardexByPedidoAndMovimientoGroupByPresentacion(pedidoId,
					com.coagronet.kardex.movimientos.MovimientoConst.ENTRADA)
			.stream()
			.collect(Collectors.toMap(ArticuloKardexRepository.RowCantidad::getPresentacionId,
					r -> safe(r.getCantidad())));

		for (var e : pedidas.entrySet()) {
			Long presentacionId = e.getKey();
			double cantPedida = e.getValue();
			double cantEntrada = entradas.getOrDefault(presentacionId, 0.0);
			// requiere: cantEntrada >= cantPedida - EPS
			if (cantEntrada + EPS < cantPedida)
				return false;
		}
		return true;
	}

	boolean validarMovimientosKardex(Long pedidoId) {
		return articuloKardexRepository.existsItemsByPedidoAndMovimiento(pedidoId,
				com.coagronet.kardex.movimientos.MovimientoConst.ENTRADA);
	}

	private static double safe(Double v) {
		if (v == null || v.isNaN() || v.isInfinite())
			return 0.0;
		return v;
	}

}
