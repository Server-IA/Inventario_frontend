package com.coagronet.unidad.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.unidad.dtos.UnidadDTO;
import com.coagronet.unidad.mappers.UnidadMapper;
import com.coagronet.unidad.repositories.UnidadRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UnidadService {

	private final UnidadRepository unidadRepository;

	private final UnidadMapper unidadMapper;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public Page<UnidadDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return unidadRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(unidadMapper::toDTO);
	}

	public Optional<UnidadDTO> findById(Long requestId) {
		return unidadRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(unidadMapper::toDTO);
	}

	@Transactional
	public UnidadDTO create(UnidadDTO unidadDTO) {
		estadoRepository.findById(unidadDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

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
