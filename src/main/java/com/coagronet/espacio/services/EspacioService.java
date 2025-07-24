package com.coagronet.espacio.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.espacio.dtos.EspacioDTO;
import com.coagronet.espacio.mappers.EspacioMapper;
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.bloque.repositories.BloqueRepository;
import com.coagronet.tipoEspacio.repositories.TipoEspacioRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspacioService {

	private final EspacioMapper espacioMapper;

	private final EspacioRepository espacioRepository;

	private final UserEmpresaService userEmpresaService;

	private final BloqueRepository bloqueRepository;

	private final TipoEspacioRepository tipoEspacioRepository;

	private final EstadoRepository estadoRepository;

	public List<EspacioDTO> findAll() {
		return espacioRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(espacioMapper::toListDto)
			.collect(Collectors.toList());
	}

	public Optional<EspacioDTO> findById(Long requestedId) {
		return espacioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(espacioMapper::toListDto);
	}

	public EspacioDTO create(EspacioDTO espacioDTO) {
		bloqueRepository
			.findByIdAndEmpresaId(espacioDTO.getBloqueId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El bloque no es válido"));

		tipoEspacioRepository
			.findByIdAndEmpresaId(espacioDTO.getTipoEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El tipo de espacio no es válido"));

		estadoRepository.findById(espacioDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		espacioDTO.setId(null);
		espacioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return espacioMapper.toDTO(espacioRepository.save(espacioMapper.toEntity(espacioDTO)));
	}

	public void update(Long requestedId, EspacioDTO espacioDTO) {
		espacioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Espacio no encontrado"));

		bloqueRepository
			.findByIdAndEmpresaId(espacioDTO.getBloqueId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El bloque no es válida"));

		tipoEspacioRepository
			.findByIdAndEmpresaId(espacioDTO.getTipoEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El tipo de espacio no es válido"));

		estadoRepository.findById(espacioDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		espacioDTO.setId(requestedId);
		espacioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		espacioRepository.save(espacioMapper.toEntity(espacioDTO));
	}

	public void delete(Long id) {
		espacioRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Espacio no encontrado"));

		espacioRepository.deleteById(id);
	}

}
