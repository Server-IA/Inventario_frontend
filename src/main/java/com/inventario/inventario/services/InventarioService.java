package com.inventario.inventario.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.inventario.dtos.InventarioDTO;
import com.inventario.inventario.mappers.InventarioMapper;
import com.inventario.inventario.repositories.InventarioRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {

	private final InventarioRepository inventarioRepository;

	private final EstadoRepository estadoRepository;

	private final InventarioMapper inventarioMapper;

	private final UserEmpresaService userEmpresaService;

	public Page<InventarioDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return inventarioRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
				.map(inventarioMapper::toDTO);
	}

	public List<InventarioDTO> findByDateBetween(LocalDateTime inicio, LocalDateTime fin) {
		return inventarioRepository
				.findByEmpresaIdAndFechaHoraBetweenOrderByFechaHoraAsc(
						userEmpresaService.getEmpresaIdFromCurrentRequest(),
						inicio, fin)
				.stream()
				.map(inventarioMapper::toDTO)
				.collect(Collectors.toList());
	}

	public Optional<InventarioDTO> findById(Long requestedId) {
		return inventarioRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(inventarioMapper::toDTO);
	}

	@Transactional
	public InventarioDTO create(InventarioDTO inventarioDTO) {
		estadoRepository.findById(inventarioDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("Estado no encontrado o no v?lido"));

		inventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return inventarioMapper.toDTO(inventarioRepository.save(inventarioMapper.toEntity(inventarioDTO)));
	}

	@Transactional
	public void update(Long requestedId, InventarioDTO inventarioDTO) {
		inventarioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Inventario no encontrada en su empresa"));

		estadoRepository.findById(inventarioDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v?lido"));

		inventarioDTO.setId(requestedId);
		inventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		inventarioRepository.save(inventarioMapper.toEntity(inventarioDTO));
	}

	@Transactional
	public void delete(Long requestedId) {
		inventarioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Inventario no encontrada en su empresa"));

		inventarioRepository.deleteById(requestedId);
	}

}
