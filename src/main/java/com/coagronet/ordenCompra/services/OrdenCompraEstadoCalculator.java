package com.coagronet.ordenCompra.services;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloOrdenCompra.ArticuloOrdenCompra;
import com.coagronet.estado.Estado;
import com.coagronet.ordenCompra.constantes.OrdenCompraConstantes;
import com.coagronet.validator.EntidadValidatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenCompraEstadoCalculator {
    private final EntidadValidatorFacade entidadValidatorFacade;

    public Estado calcularNuevoEstado(List<ArticuloOrdenCompra> articulosOC,
                                      List<ArticuloKardex> articulosKardex) {

        Map<Long, Double> cantidadesRecepcionadas = articulosKardex.stream()
                .collect(Collectors.groupingBy(
                        ak -> ak.getPresentacionProducto().getId(),
                        Collectors.summingDouble(ArticuloKardex::getCantidad)
                ));

        boolean todosCompletos = true;
        boolean hayParcial = false;
        boolean todosEnCero = true;

        for (ArticuloOrdenCompra aoc : articulosOC) {
            Double cantidadSolicitada = aoc.getCantidad();
            Double recibido = cantidadesRecepcionadas.getOrDefault(aoc.getPresentacionProducto().getId(), 0.0);

            if (recibido > 0) todosEnCero = false;
            if (recibido < cantidadSolicitada) {
                todosCompletos = false;
                if (recibido > 0) hayParcial = true;
            }
        }

        if (todosCompletos && !todosEnCero) {
            return entidadValidatorFacade.validarEstadoParaOrdenCompra(
                    OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_ENTREGA_TOTAL);
        }
        if (hayParcial) {
            return entidadValidatorFacade.validarEstadoParaOrdenCompra(
                    OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_ENTREGA_PARCIAL);
        }
        return null;
    }
}
