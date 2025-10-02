package com.coagronet.validator.inventario.entidades;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PedidoValidator {

    private final PedidoRepository pedidoRepository;

    public Pedido validarPedido(Long pedidoId, Long empresaId){
        return pedidoRepository.findByIdAndEmpresaId(pedidoId, empresaId)
                .orElseThrow(()-> new NotFoundException("pedido.not-found", pedidoId));
    }
}
