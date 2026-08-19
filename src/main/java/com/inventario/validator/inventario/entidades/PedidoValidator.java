package com.inventario.validator.inventario.entidades;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.pedido.Pedido;
import com.inventario.pedido.repositories.PedidoRepository;
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
