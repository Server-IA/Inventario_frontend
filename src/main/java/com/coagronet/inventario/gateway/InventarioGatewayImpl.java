package com.coagronet.inventario.gateway;

// Importación estática para limpiar el código
import static com.coagronet.kardex.movimientos.MovimientoConst.ENTRADA;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.articuloPedido.repositories.ArticuloPedidoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventarioGatewayImpl implements InventarioGateway {

	private static final BigDecimal EPS = new BigDecimal("1e-6");

	private final ArticuloPedidoRepository articuloPedidoRepository;

	private final ArticuloKardexRepository articuloKardexRepository;

	@Override
	@Transactional(readOnly = true)
	public Result validarRequisitosParaCompletar(Long pedidoId) {
		if (!validarMovimientosKardex(pedidoId)) {
			return Result.builder()
				.ok(false)
				.motivoFallo("Faltan asientos de inventario (Kardex ENTRADA) para el pedido.")
				.build();
		}

		if (!validarTodoRecibido(pedidoId)) {
			return Result.builder()
				.ok(false)
				.motivoFallo("Existen ítems pendientes por recibir (comparativo Pedido vs Kardex ENTRADA).")
				.build();
		}

		return Result.builder().ok(true).build();
	}

	private boolean validarTodoRecibido(Long pedidoId) {
		Map<Long, BigDecimal> pedidas = articuloPedidoRepository.sumCantidadesPedidasGroupByPresentacion(pedidoId)
			.stream()
			.collect(Collectors.<ArticuloPedidoRepository.RowCantidad, Long, BigDecimal>toMap(
					ArticuloPedidoRepository.RowCantidad::getPresentacionId, r -> safe(r.getCantidad())));

		if (pedidas.isEmpty()) {
			return false;
		}

		Map<Long, BigDecimal> entradas = articuloKardexRepository
			.sumCantidadesKardexByPedidoAndMovimientoGroupByPresentacion(pedidoId, ENTRADA)
			.stream()
			.collect(Collectors.<ArticuloKardexRepository.RowCantidad, Long, BigDecimal>toMap(
					ArticuloKardexRepository.RowCantidad::getPresentacionId, r -> safe(r.getCantidad())));

		for (Map.Entry<Long, BigDecimal> e : pedidas.entrySet()) {
			Long presentacionId = e.getKey();
			BigDecimal cantPedida = e.getValue();
			BigDecimal cantEntrada = entradas.getOrDefault(presentacionId, BigDecimal.ZERO);

			if (cantEntrada.add(EPS).compareTo(cantPedida) < 0) {
				return false;
			}
		}
		return true;
	}

	private boolean validarMovimientosKardex(Long pedidoId) {
		return articuloKardexRepository.existsItemsByPedidoAndMovimiento(pedidoId, ENTRADA);
	}

	private static BigDecimal safe(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

}