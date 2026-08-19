package com.inventario.tipoProduccion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.tipoProduccion.dtos.TipoProduccionDTO;
import com.inventario.tipoProduccion.mappers.TipoProduccionMapper;
import com.inventario.tipoProduccion.repositories.TipoProduccionRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoProduccionService {

	private final TipoProduccionMapper tipoProduccionMapper;

	private final TipoProduccionRepository tipoProduccionRepository;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public List<TipoProduccionDTO> findAll() {
		return tipoProduccionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream()
				.map(tipoProduccionMapper::toListDTO)
				.collect(Collectors.toList());
	}

	public Optional<TipoProduccionDTO> findById(Long requestedId) {
		return tipoProduccionRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(tipoProduccionMapper::toListDTO);
	}

	public TipoProduccionDTO create(TipoProduccionDTO tipoProduccionDTO) {
		estadoRepository.findById(tipoProduccionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		tipoProduccionDTO.setId(null);
		tipoProduccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return tipoProduccionMapper
				.toDTO(tipoProduccionRepository.save(tipoProduccionMapper.toEntity(tipoProduccionDTO)));
	}

	public void update(Long requestedId, TipoProduccionDTO tipoProduccionDTO) {
		tipoProduccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("El tipo de producción no fue encontrado."));

		estadoRepository.findById(tipoProduccionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		tipoProduccionDTO.setId(requestedId);
		tipoProduccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		tipoProduccionRepository.save(tipoProduccionMapper.toEntity(tipoProduccionDTO));
	}

	public void delete(Long id) {
		tipoProduccionRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("El tipo de producción no fue encontrado."));

		tipoProduccionRepository.deleteById(id);
	}

}
