package com.inventario.tipoMovimiento.services;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.tipoMovimiento.dtos.TipoMovimientoDTO;
import com.inventario.tipoMovimiento.mappers.TipoMovimientoMapper;
import com.inventario.tipoMovimiento.repositories.TipoMovimientoRepository;
import com.inventario.utils.UserEmpresaService;
import com.inventario.validator.EntidadValidatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoMovimientoService {

	private final TipoMovimientoRepository tipoMovimientoRepository;
	private final TipoMovimientoMapper tipoMovimientoMapper;
	private final EstadoRepository estadoRepository;
	private final UserEmpresaService userEmpresaService;
	private final EntidadValidatorFacade entidadValidatorFacade;

	public List<TipoMovimientoDTO> findAll() {

		return tipoMovimientoRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream()
				.map(tipoMovimientoMapper::toDto)
				.collect(Collectors.toList());
	}

	public Optional<TipoMovimientoDTO> findById(Long requestedId) {

		return tipoMovimientoRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(tipoMovimientoMapper::toDto);
	}

	@Transactional
	public TipoMovimientoDTO create(TipoMovimientoDTO tipoMovimientoDTO) {

		entidadValidatorFacade.validarEstadoGeneral(tipoMovimientoDTO.getEstadoId());
		entidadValidatorFacade.validarMovimiento(tipoMovimientoDTO.getMovimientoId());

		tipoMovimientoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return tipoMovimientoMapper
				.toDto(tipoMovimientoRepository.save(tipoMovimientoMapper.toEntity(tipoMovimientoDTO)));
	}

	@Transactional
	public void update(Long requestedId, TipoMovimientoDTO tipoMovimientoDTO) {

		tipoMovimientoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("TipoMovimiento no encontrada o no válida"));

		estadoRepository.findById(tipoMovimientoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		tipoMovimientoDTO.setId(requestedId);
		tipoMovimientoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		tipoMovimientoRepository.save(tipoMovimientoMapper.toEntity(tipoMovimientoDTO));
	}

	@Transactional
	public void delete(Long requestId) {

		tipoMovimientoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("TipoMovimiento no encontrado o no válido"));

		tipoMovimientoRepository.deleteById(requestId);
	}

}
