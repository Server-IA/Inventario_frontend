package com.coagronet.seccion.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.seccion.dtos.SeccionDTO;
import com.coagronet.seccion.mapper.SeccionMapper;
import com.coagronet.seccion.repositories.SeccionRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeccionService {

	private final UserEmpresaService userEmpresaService;

	private final SeccionMapper seccionMapper;

	private final SeccionRepository seccionRepository;

	private final EstadoRepository estadoRepository;

	private final EspacioRepository espacioRepository;

	public Page<SeccionDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		return seccionRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(seccionMapper::toListDTO);
	}

	public Optional<SeccionDTO> findById(Long requestedId) {
		return seccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(seccionMapper::toListDTO);
	}

	public SeccionDTO create(SeccionDTO seccionDTO) {
		espacioRepository
				.findByIdAndEmpresaId(seccionDTO.getEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El espacio no es v�lido"));

		estadoRepository.findById(seccionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		seccionDTO.setId(null);
		seccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return seccionMapper.toDTO(seccionRepository.save(seccionMapper.toEntity(seccionDTO)));
	}

	public void update(Long requestedId, SeccionDTO seccionDTO) {
		seccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("La secci�n no fue encontrada."));

		espacioRepository
				.findByIdAndEmpresaId(seccionDTO.getEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El espacio no es v�lido"));

		estadoRepository.findById(seccionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		seccionDTO.setId(requestedId);
		seccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		seccionRepository.save(seccionMapper.toEntity(seccionDTO));
	}

	public void delete(Long id) {
		seccionRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("La secci�n no fue encontrada."));

		seccionRepository.deleteById(id);
	}

}