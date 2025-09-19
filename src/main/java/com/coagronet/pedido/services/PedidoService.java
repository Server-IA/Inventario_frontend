package com.coagronet.pedido.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.dtos.PedidoDTO;
import com.coagronet.pedido.mappers.PedidoMapper;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	private final AlmacenRepository almacenRepository;

	private final ProduccionRepository produccionRepository;

	private final EstadoRepository estadoRepository;

	private final PedidoMapper pedidoMapper;

	private final UserEmpresaService userEmpresaService;

	public Page<PedidoDTO> findAll(Pageable pageable) {
		return pedidoRepository
			.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable)
			.map(pedidoMapper::toDto);
	}

	public PedidoDTO findById(Long requestedId) {
		return pedidoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(pedidoMapper::toDto)
			.orElseThrow(() -> new NotFoundException("pedido.not-found", requestedId));
	}

	@Transactional
	public PedidoDTO create(PedidoDTO pedidoDTO) {
		almacenRepository
			.findByIdAndEmpresaId(pedidoDTO.getAlmacenId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("almacen.not-found", pedidoDTO.getAlmacenId()));

		produccionRepository
			.findByIdAndEmpresaId(pedidoDTO.getProduccionId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("produccion.not-found", pedidoDTO.getProduccionId()));

		estadoRepository.findByIdAndEstadoCategoriaId(pedidoDTO.getEstadoId(), 2L)
			.orElseThrow(
					() -> new NotFoundException("validation.pedido.estado.invalid-category", pedidoDTO.getEstadoId()));

		pedidoDTO.setId(null);
		pedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return pedidoMapper.toDto(pedidoRepository.save(pedidoMapper.toEntity(pedidoDTO)));
	}

	@Transactional
	public void update(Long requestedId, PedidoDTO pedidoDTO) {
		pedidoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("pedido.not-found", requestedId));

		almacenRepository
			.findByIdAndEmpresaId(pedidoDTO.getAlmacenId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("almacen.not-found", pedidoDTO.getAlmacenId()));

		produccionRepository
			.findByIdAndEmpresaId(pedidoDTO.getProduccionId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("produccion.not-found", pedidoDTO.getProduccionId()));

		estadoRepository.findByIdAndEstadoCategoriaId(pedidoDTO.getEstadoId(), 2L)
			.orElseThrow(
					() -> new NotFoundException("validation.pedido.estado.invalid-category", pedidoDTO.getEstadoId()));

		pedidoDTO.setId(requestedId);
		pedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		pedidoRepository.save(pedidoMapper.toEntity(pedidoDTO));
	}

	@Transactional
	public void delete(Long requestedId) {
		pedidoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("pedido.not-found", requestedId));

		pedidoRepository.deleteById(requestedId);

	}

}
