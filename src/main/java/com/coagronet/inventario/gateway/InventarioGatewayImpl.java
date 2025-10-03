package com.coagronet.inventario.gateway;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.articuloPedido.repositories.ArticuloPedidoRepository;
import com.coagronet.kardex.movimientos.MovimientoConst;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventarioGatewayImpl implements InventarioGateway {

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

		if (pedidas.isEmpty()) {
			return false;
		}

		var entradas = articuloKardexRepository
			.sumCantidadesKardexByPedidoAndMovimientoGroupByPresentacion(pedidoId, MovimientoConst.ENTRADA)
			.stream()
			.collect(Collectors.toMap(ArticuloKardexRepository.RowCantidad::getPresentacionId,
					r -> safe(r.getCantidad())));

		for (var e : pedidas.entrySet()) {
			Long presentacionId = e.getKey();
			BigDecimal cantPedida = e.getValue();
			BigDecimal cantEntrada = entradas.getOrDefault(presentacionId, BigDecimal.ZERO);
			if (cantEntrada.compareTo(cantPedida) < 0)
				return false;
		}
		return true;
	}

	boolean validarMovimientosKardex(Long pedidoId) {
		return articuloKardexRepository.existsItemsByPedidoAndMovimiento(pedidoId, MovimientoConst.ENTRADA);
	}

	private static BigDecimal safe(BigDecimal v) {
		return v != null ? v : BigDecimal.ZERO;
	}

}
