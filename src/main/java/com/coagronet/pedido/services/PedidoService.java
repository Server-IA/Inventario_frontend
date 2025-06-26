package com.coagronet.pedido.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.dtos.PedidoDTO;
import com.coagronet.pedido.mappers.PedidoMapper;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final UserEmpresaService userEmpresaService;
    private final EstadoRepository estadoRepository;

    public List<PedidoDTO> findAll() {
        return pedidoRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(pedidoMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<PedidoDTO> findById(Long requestId) {
        return pedidoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(pedidoMapper::toDto);
    }

    @Transactional
    public PedidoDTO create(PedidoDTO pedidoDTO) {
        estadoRepository.findById(pedidoDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado o no válido"));

        pedidoDTO.setId(null);
        pedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return pedidoMapper.toDto(pedidoRepository.save(pedidoMapper.toEntity(pedidoDTO)));
    }

    @Transactional
    public void update(Long requestId, PedidoDTO pedidoDTO) {
        pedidoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado o no válido"));

        estadoRepository.findById(pedidoDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado o no válido"));

        pedidoDTO.setId(requestId);
        pedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        pedidoRepository.save(pedidoMapper.toEntity(pedidoDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        pedidoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado o no válido"));

        pedidoRepository.deleteById(requestId);

    }

}
