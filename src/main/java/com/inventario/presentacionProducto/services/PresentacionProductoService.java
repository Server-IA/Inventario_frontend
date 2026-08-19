package com.inventario.presentacionProducto.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.presentacionProducto.dtos.PresentacionProductoDTO;
import com.inventario.presentacionProducto.mappers.PresentacionProductoMapper;
import com.inventario.presentacionProducto.repositories.PresentacionProductoRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresentacionProductoService {

	private final PresentacionProductoRepository presentacionProductoRepository;

	private final EstadoRepository estadoRepository;

	private final PresentacionProductoMapper presentacionProductoMapper;

	private final UserEmpresaService userEmpresaService;

	public Page<PresentacionProductoDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return presentacionProductoRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(presentacionProductoMapper::toDto);
	}

	public Optional<PresentacionProductoDTO> findById(Long requestId) {
		return presentacionProductoRepository
			.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(presentacionProductoMapper::toDto);
	}

	@Transactional
	public PresentacionProductoDTO create(PresentacionProductoDTO presentacionProductoDTO) {
		presentacionProductoDTO.setId(null);
		presentacionProductoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return presentacionProductoMapper
			.toDto(presentacionProductoRepository.save(presentacionProductoMapper.toEntity(presentacionProductoDTO)));
	}

	@Transactional
	public void update(Long requestId, PresentacionProductoDTO presentacionProductoDTO) {
		presentacionProductoRepository
			.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Producto Presentacion no encontrado"));

		estadoRepository.findById(presentacionProductoDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		presentacionProductoDTO.setId(requestId);
		presentacionProductoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		presentacionProductoRepository.save(presentacionProductoMapper.toEntity(presentacionProductoDTO));

	}

	@Transactional
	public void delete(Long requestId) {
		presentacionProductoRepository
			.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Producto Presentacion no encontrada"));

		presentacionProductoRepository.deleteById(requestId);
	}

}
