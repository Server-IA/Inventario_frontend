package com.coagronet.pedido.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.dtos.PedidoDTO;
import com.coagronet.pedido.mappers.PedidoMapper;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	private final PedidoMapper pedidoMapper;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public Page<PedidoDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return pedidoRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(pedidoMapper::toDto);
	}

	public Optional<PedidoDTO> findById(Long requestId) {
		return pedidoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(pedidoMapper::toDto);
	}

	@Transactional
	public PedidoDTO create(PedidoDTO pedidoDTO) {
		estadoRepository.findById(pedidoDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado o no v�lido"));

		pedidoDTO.setId(null);
		pedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return pedidoMapper.toDto(pedidoRepository.save(pedidoMapper.toEntity(pedidoDTO)));
	}

	@Transactional
	public void update(Long requestId, PedidoDTO pedidoDTO) {
		pedidoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Pedido no encontrado o no v�lido"));

		estadoRepository.findById(pedidoDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado o no v�lido"));

		pedidoDTO.setId(requestId);
		pedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		pedidoRepository.save(pedidoMapper.toEntity(pedidoDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		pedidoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Pedido no encontrado o no v�lido"));

		pedidoRepository.deleteById(requestId);

	}

}
