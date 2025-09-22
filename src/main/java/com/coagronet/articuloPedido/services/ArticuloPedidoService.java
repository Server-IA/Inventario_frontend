package com.coagronet.articuloPedido.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.articuloPedido.dtos.ArticuloPedidoDTO;
import com.coagronet.articuloPedido.mappers.ArticuloPedidoMapper;
import com.coagronet.articuloPedido.repositories.ArticuloPedidoRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticuloPedidoService {

	private final UserEmpresaService userEmpresaService;

	private final ArticuloPedidoMapper articuloPedidoMapper;

	private final ArticuloPedidoRepository articuloPedidoRepository;

	private final PedidoRepository pedidoRepository;

	private final PresentacionProductoRepository presentacionProductoRepository;

	private final EstadoRepository estadoRepository;

	public Page<ArticuloPedidoDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return articuloPedidoRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(articuloPedidoMapper::toListDTO);
	}

	public List<ArticuloPedidoDTO> findAllByPedidoId(Long pedidoId) {
		return articuloPedidoRepository
			.findByEmpresaIdAndPedidoIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pedidoId)
			.stream()
			.map(articuloPedidoMapper::toListDTO)
			.collect(Collectors.toList());
	}

	public Optional<ArticuloPedidoDTO> findById(Long requestedId) {
		return articuloPedidoRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(articuloPedidoMapper::toListDTO);
	}

	public ArticuloPedidoDTO create(ArticuloPedidoDTO articuloPedidoDTO) {
		pedidoRepository
			.findByIdAndEmpresaId(articuloPedidoDTO.getPedidoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El pedido no es válido."));

		presentacionProductoRepository
			.findByIdAndEmpresaId(articuloPedidoDTO.getPresentacionProductoId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("La presentación de producto no es válida."));

		estadoRepository.findById(articuloPedidoDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		articuloPedidoDTO.setId(null);
		articuloPedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return articuloPedidoMapper
			.toDTO(articuloPedidoRepository.save(articuloPedidoMapper.toEntity(articuloPedidoDTO)));
	}

	public void update(Long requestedId, ArticuloPedidoDTO articuloPedidoDTO) {
		articuloPedidoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El artículo de pedido no fue encontrado."));

		pedidoRepository
			.findByIdAndEmpresaId(articuloPedidoDTO.getPedidoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El pedido no es válido."));

		presentacionProductoRepository
			.findByIdAndEmpresaId(articuloPedidoDTO.getPresentacionProductoId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("La presentación de producto no es válida."));

		estadoRepository.findById(articuloPedidoDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		articuloPedidoDTO.setId(requestedId);
		articuloPedidoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		articuloPedidoRepository.save(articuloPedidoMapper.toEntity(articuloPedidoDTO));
	}

	public void delete(Long id) {
		articuloPedidoRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El artículo de pedido no fue encontrado."));

		articuloPedidoRepository.deleteById(id);
	}

}
