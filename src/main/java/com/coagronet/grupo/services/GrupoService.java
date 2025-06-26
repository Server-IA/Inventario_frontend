package com.coagronet.grupo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.grupo.mappers.GrupoMapper;
import com.coagronet.grupo.repositories.GrupoRepository;
import com.coagronet.grupo.dtos.GrupoDTO;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrupoService {

	private final GrupoMapper grupoMapper;
	private final GrupoRepository grupoRepository;
	private final UserEmpresaService userEmpresaService;
	private final EstadoRepository estadoRepository;

	public List<GrupoDTO> findAll() {
		return grupoRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest()).stream()
				.map(grupoMapper::toListDto)
				.collect(Collectors.toList());
	}

	public Optional<GrupoDTO> findById(Long requestedId) {
		return grupoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(grupoMapper::toListDto);
	}

	public GrupoDTO create(GrupoDTO grupoDTO) {
		estadoRepository.findById(grupoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		grupoDTO.setId(null);
		grupoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return grupoMapper.toDTO(grupoRepository.save(grupoMapper.toEntity(grupoDTO)));
	}

	public void update(Long requestedId, GrupoDTO grupoDTO) {
		grupoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

		estadoRepository.findById(grupoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		grupoDTO.setId(requestedId);
		grupoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		grupoRepository.save(grupoMapper.toEntity(grupoDTO));
	}

	public void delete(Long id) {
		grupoRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

		grupoRepository.deleteById(id);
	}

}
