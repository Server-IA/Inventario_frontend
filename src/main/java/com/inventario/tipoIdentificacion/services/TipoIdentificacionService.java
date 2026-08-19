package com.inventario.tipoIdentificacion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.tipoIdentificacion.dtos.TipoIdentificacionDTO;
import com.inventario.tipoIdentificacion.mappers.TipoIdentificacionMapper;
import com.inventario.tipoIdentificacion.repositories.TipoIdentificacionRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoIdentificacionService {

	private final TipoIdentificacionRepository tipoIdentificacionRepository;

	private final TipoIdentificacionMapper tipoIdentificacionMapper;

	private final EstadoRepository estadoRepository;

	public List<TipoIdentificacionDTO> findAll() {
		return tipoIdentificacionRepository.findAll()
				.stream()
				.map(tipoIdentificacionMapper::toDTO)
				.collect(Collectors.toList());
	}

	public List<TipoIdentificacionDTO> findAllAvailable() {
		return tipoIdentificacionRepository.findByEstadoIdNotOrderByIdAsc(2L)
				.stream()
				.map(tipoIdentificacionMapper::toDTO)
				.collect(Collectors.toList());
	}

	public Optional<TipoIdentificacionDTO> findById(Long requestedId) {
		return tipoIdentificacionRepository.findById(requestedId).map(tipoIdentificacionMapper::toDTO);
	}

	public TipoIdentificacionDTO create(TipoIdentificacionDTO tipoIdentificacionDTO) {
		estadoRepository.findById(tipoIdentificacionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		tipoIdentificacionDTO.setId(null);

		return tipoIdentificacionMapper
				.toDTO(tipoIdentificacionRepository.save(tipoIdentificacionMapper.toEntity(tipoIdentificacionDTO)));
	}

	public void update(Long requestedId, TipoIdentificacionDTO tipoIdentificacionDTO) {
		tipoIdentificacionRepository.findById(requestedId)
				.orElseThrow(() -> new BadRequestException("El tipo de identificaci�n no existe"));

		estadoRepository.findById(tipoIdentificacionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		tipoIdentificacionDTO.setId(requestedId);

		tipoIdentificacionRepository.save(tipoIdentificacionMapper.toEntity(tipoIdentificacionDTO));
	}

	public void delete(Long id) {
		tipoIdentificacionRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("El tipo de identificaci�n no existe"));

		tipoIdentificacionRepository.deleteById(id);
	}

}
