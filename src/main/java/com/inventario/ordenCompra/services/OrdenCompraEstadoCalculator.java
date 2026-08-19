package com.inventario.ordenCompra.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.articuloKardex.ArticuloKardex;
import com.inventario.articuloOrdenCompra.ArticuloOrdenCompra;
import com.inventario.estado.Estado;
import com.inventario.ordenCompra.constantes.OrdenCompraConstantes;
import com.inventario.validator.EntidadValidatorFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenCompraEstadoCalculator {

	private final EntidadValidatorFacade entidadValidatorFacade;

	public Estado calcularNuevoEstado(List<ArticuloOrdenCompra> articulosOC, List<ArticuloKardex> articulosKardex) {

		// 1. Agrupar y sumar usando BigDecimal de forma segura sin perder precisión
		Map<Long, BigDecimal> cantidadesRecepcionadas = articulosKardex.stream()
			.collect(Collectors.groupingBy(ak -> ak.getPresentacionProducto().getId(),
					Collectors.reducing(BigDecimal.ZERO, ArticuloKardex::getCantidad, BigDecimal::add)));

		boolean todosCompletos = true;
		boolean todosEnCero = true;

		for (ArticuloOrdenCompra aoc : articulosOC) {
			// Nota: Se asume que aoc.getCantidad() también fue migrado a BigDecimal.
			// Si ArticuloOrdenCompra aún usa Double, debes migrarlo en la entidad para
			// mantener consistencia.
			BigDecimal cantidadSolicitada = aoc.getCantidad();
			BigDecimal recibido = cantidadesRecepcionadas.getOrDefault(aoc.getPresentacionProducto().getId(),
					BigDecimal.ZERO);

			// Si al menos uno recibió algo, ya no todo es cero (recibido > 0)
			if (recibido.compareTo(BigDecimal.ZERO) > 0) {
				todosEnCero = false;
			}

			// Si al menos uno recibió menos de lo solicitado, la orden ya no está
			// completa (recibido < cantidadSolicitada)
			if (recibido.compareTo(cantidadSolicitada) < 0) {
				todosCompletos = false;
			}
		}

		// 2. Lógica de Estados Simplificada e Infalible

		// Si no se ha recibido absolutamente nada, asumimos que no hay cambio de estado
		// (o retorna PENDIENTE)
		if (todosEnCero) {
			return null;
		}

		// Si se recibió mercadería y ninguna línea está incompleta
		if (todosCompletos) {
			return entidadValidatorFacade
				.validarEstadoParaOrdenCompra(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_ENTREGA_TOTAL);
		}

		// Si no es "Todo en Cero" y tampoco es "Todo Completo",
		// por descarte matemático, es una Entrega Parcial.
		return entidadValidatorFacade
			.validarEstadoParaOrdenCompra(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_ENTREGA_PARCIAL);
	}

}
