package com.coagronet.ordenCompra.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;
import com.coagronet.ordenCompra.mappers.OrdenCompraMapper;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.proveedor.repositories.ProveedorRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

	private final OrdenCompraRepository ordenCompraRepository;

	private final OrdenCompraMapper ordenCompraMapper;

	private final PedidoRepository pedidoRepository;

	private final ProveedorRepository proveedorRepository;

	private final EstadoRepository estadoRepository;

	private final UserEmpresaService userEmpresaService;

	public Page<OrdenCompraDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return ordenCompraRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(ordenCompraMapper::toListDTO);
	}

	public Optional<OrdenCompraDTO> findById(Long requestedId) {
		return ordenCompraRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(ordenCompraMapper::toListDTO);
	}

	@Transactional
	public OrdenCompraDTO create(OrdenCompraDTO ordenCompraDTO) {

		pedidoRepository
			.findByIdAndEmpresaId(ordenCompraDTO.getPedidoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El pedido no es válido."));

		proveedorRepository
			.findByIdAndEmpresaId(ordenCompraDTO.getProveedorId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El proveedor no es válido."));

		estadoRepository.findById(ordenCompraDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		ordenCompraDTO.setId(null);
		ordenCompraDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return ordenCompraMapper.toDTO(ordenCompraRepository.save(ordenCompraMapper.toEntity(ordenCompraDTO)));
	}

	@Transactional
	public void update(Long requestedId, OrdenCompraDTO ordenCompraDTO) {
		ordenCompraRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Orden de compra no encontrada."));

		pedidoRepository
			.findByIdAndEmpresaId(ordenCompraDTO.getPedidoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El pedido no es válido."));

		proveedorRepository
			.findByIdAndEmpresaId(ordenCompraDTO.getProveedorId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El proveedor no es válido."));

		estadoRepository.findById(ordenCompraDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		ordenCompraDTO.setId(requestedId);
		ordenCompraDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		ordenCompraRepository.save(ordenCompraMapper.toEntity(ordenCompraDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		ordenCompraRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Orden de compra no encontrada."));

		ordenCompraRepository.deleteById(requestId);
	}

}
