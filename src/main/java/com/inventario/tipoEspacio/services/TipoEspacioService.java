package com.inventario.tipoEspacio.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.tipoEspacio.dtos.TipoEspacioDTO;
import com.inventario.tipoEspacio.mappers.TipoEspacioMapper;
import com.inventario.tipoEspacio.repositories.TipoEspacioRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoEspacioService {

	private final TipoEspacioRepository tipoEspacioRepository;

	private final TipoEspacioMapper tipoEspacioMapper;

	private final EstadoRepository estadoRepository;

	private final UserEmpresaService userEmpresaService;

	public List<TipoEspacioDTO> findAll() {
		return tipoEspacioRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(tipoEspacioMapper::toListDto)
			.collect(Collectors.toList());
	}

	public List<TipoEspacioDTO> findAllAvailable() {
		return tipoEspacioRepository
			.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), 2L)
			.stream()
			.map(tipoEspacioMapper::toListDto)
			.collect(Collectors.toList());
	}

	public Optional<TipoEspacioDTO> findById(Long requestedId) {
		return tipoEspacioRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(tipoEspacioMapper::toListDto);
	}

	public TipoEspacioDTO create(TipoEspacioDTO tipoEspacioDTO) {
		estadoRepository.findById(tipoEspacioDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		tipoEspacioDTO.setId(null);
		tipoEspacioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return tipoEspacioMapper.toDTO(tipoEspacioRepository.save(tipoEspacioMapper.toEntity(tipoEspacioDTO)));
	}

	public void update(Long requestedId, TipoEspacioDTO tipoEspacioDTO) {
		tipoEspacioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Tipo de espacio no encontrado"));

		estadoRepository.findById(tipoEspacioDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		tipoEspacioDTO.setId(requestedId);
		tipoEspacioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		tipoEspacioRepository.save(tipoEspacioMapper.toEntity(tipoEspacioDTO));
	}

	public void delete(Long id) {
		tipoEspacioRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Tipo de espacio no encontrado"));

		tipoEspacioRepository.deleteById(id);
	}

}