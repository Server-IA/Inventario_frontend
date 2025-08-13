package com.coagronet.producto.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.producto.dtos.ProductoDTO;
import com.coagronet.producto.mappers.ProductoMapper;
import com.coagronet.producto.repositories.ProductoRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

	private final ProductoRepository productoRepository;

	private final EstadoRepository estadoRepository;

	private final ProductoMapper productoMapper;

	private final UserEmpresaService userEmpresaService;

	public Page<ProductoDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return productoRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(productoMapper::toDto);
	}

	public Optional<ProductoDTO> findById(Long requestedId) {
		return productoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(productoMapper::toDto);
	}

	@Transactional
	public ProductoDTO create(ProductoDTO productoDTO) {
		estadoRepository.findById(productoDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("Estado no encontrado o no válido"));

		productoDTO.setId(null);
		productoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return productoMapper.toDto(productoRepository.save(productoMapper.toEntity(productoDTO)));

	}

	@Transactional
	public void update(Long requestedId, ProductoDTO productoDTO) {
		productoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Producto no encontrado o no válido"));

		estadoRepository.findById(productoDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado o no válido"));

		productoDTO.setId(requestedId);
		productoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		productoRepository.save(productoMapper.toEntity(productoDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		productoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Producto no encontrado o no válido"));

		productoRepository.deleteById(requestId);
	}

}
