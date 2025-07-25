package com.coagronet.unidad.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.unidad.dtos.UnidadDTO;
import com.coagronet.unidad.mappers.UnidadMapper;
import com.coagronet.unidad.repositories.UnidadRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UnidadService {

	private final UnidadRepository unidadRepository;

	private final UnidadMapper unidadMapper;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public List<UnidadDTO> findAll() {
		return unidadRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(unidadMapper::toDTO)
			.collect(Collectors.toList());
	}

	public Optional<UnidadDTO> findById(Long requestId) {
		return unidadRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(unidadMapper::toDTO);
	}

	@Transactional
	public UnidadDTO create(UnidadDTO unidadDTO) {
		estadoRepository.findById(unidadDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		unidadDTO.setId(null);
		unidadDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return unidadMapper.toDTO(unidadRepository.save(unidadMapper.toEntity(unidadDTO)));
	}

	@Transactional
	public void update(Long requestId, UnidadDTO unidadDTO) {
		unidadRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Unidad no encontrada"));

		estadoRepository.findById(unidadDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		unidadDTO.setId(requestId);
		unidadDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
		unidadRepository.save(unidadMapper.toEntity(unidadDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		unidadRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Unidad no encontrada"));

		unidadRepository.deleteById(requestId);
	}

}
