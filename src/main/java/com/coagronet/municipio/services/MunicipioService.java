package com.coagronet.municipio.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.departamento.repositories.DepartamentoRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.municipio.dtos.MunicipioDTO;
import com.coagronet.municipio.mappers.MunicipioMapper;
import com.coagronet.municipio.repositories.MunicipioRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MunicipioService {

	private final MunicipioMapper municipioMapper;

	private final MunicipioRepository municipioRepository;

	private final DepartamentoRepository departamentoRepository;

	private final EstadoRepository estadoRepository;

	private final UserEmpresaService userEmpresaService;

	public List<MunicipioDTO> findAll(Long departamentoId) {
		return municipioRepository
			.findByDepartamentoIdAndEmpresaIdOrderByIdAsc(departamentoId,
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(municipioMapper::toListDto)
			.collect(Collectors.toList());
	}

	public Optional<MunicipioDTO> findById(Long requestedId) {
		return municipioRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(municipioMapper::toListDto);
	}

	public MunicipioDTO create(MunicipioDTO municipioDTO) {
		departamentoRepository
			.findByIdAndEmpresaId(municipioDTO.getDepartamentoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

		estadoRepository.findById(municipioDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		municipioDTO.setId(null);
		municipioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return municipioMapper.toDTO(municipioRepository.save(municipioMapper.toEntity(municipioDTO)));
	}

	public void update(Long requestedId, MunicipioDTO municipioDTO) {
		municipioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Municipio no encontrado"));

		departamentoRepository
			.findByIdAndEmpresaId(municipioDTO.getDepartamentoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

		estadoRepository.findById(municipioDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		municipioDTO.setId(requestedId);
		municipioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		municipioRepository.save(municipioMapper.toEntity(municipioDTO));
	}

	public void delete(Long id) {
		municipioRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Municipio no encontrado"));

		municipioRepository.deleteById(id);
	}

}