package com.coagronet.departamento.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.departamento.dtos.DepartamentoDTO;
import com.coagronet.departamento.mappers.DepartamentoMapper;
import com.coagronet.departamento.repositories.DepartamentoRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.pais.repositories.PaisRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

	private final DepartamentoMapper departamentoMapper;

	private final DepartamentoRepository departamentoRepository;

	private final PaisRepository paisRepository;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public List<DepartamentoDTO> findAll() {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		return departamentoRepository.findByEmpresaIdOrderByIdAsc(empresaId)
				.stream()
				.map(departamentoMapper::toListDto)
				.collect(Collectors.toList());
	}

	public Optional<DepartamentoDTO> findById(Long requestedId) {
		return departamentoRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(departamentoMapper::toListDto);
	}

	public DepartamentoDTO create(DepartamentoDTO departamentoDTO) {
		paisRepository
				.findByIdAndEmpresaId(departamentoDTO.getPaisId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(
						() -> new BadRequestException("El pa�s no es v�lido para la empresa del usuario autenticado"));

		estadoRepository.findById(departamentoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		departamentoDTO.setId(null);
		departamentoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return departamentoMapper.toDTO(departamentoRepository.save(departamentoMapper.toEntity(departamentoDTO)));
	}

	public void update(Long requestedId, DepartamentoDTO departamentoDTO) {
		departamentoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

		paisRepository
				.findByIdAndEmpresaId(departamentoDTO.getPaisId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(
						() -> new BadRequestException("El pa�s no es v�lido para la empresa del usuario autenticado"));

		departamentoDTO.setId(requestedId);
		departamentoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		departamentoRepository.save(departamentoMapper.toEntity(departamentoDTO));
	}

	public void delete(Long id) {
		departamentoRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

		departamentoRepository.deleteById(id);
	}

}