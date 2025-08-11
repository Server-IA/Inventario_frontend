package com.coagronet.espacioOcupacion.services;

import com.coagronet.espacioOcupacion.dtos.EspacioOcupacionDTO;
import com.coagronet.espacioOcupacion.mappers.EspacioOcupacionMapper;
import com.coagronet.espacioOcupacion.repositories.EspacioOcupacionRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EspacioOcupacionService {

	private final EspacioOcupacionRepository espacioOcupacionRepository;

	private final EstadoRepository estadoRepository;

	private final EspacioOcupacionMapper espacioOcupacionMapper;

	private final UserEmpresaService userEmpresaService;

	public Page<EspacioOcupacionDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return espacioOcupacionRepository
			.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(espacioOcupacionMapper::toDTO);
	}

	public Optional<EspacioOcupacionDTO> findById(Long requestedId) {
		return espacioOcupacionRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(espacioOcupacionMapper::toDTO);
	}

	@Transactional
	public EspacioOcupacionDTO create(EspacioOcupacionDTO espacioOcupacionDTO) {
		estadoRepository.findById(espacioOcupacionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		espacioOcupacionDTO.setId(null);
		espacioOcupacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return espacioOcupacionMapper
			.toDTO(espacioOcupacionRepository.save(espacioOcupacionMapper.toEntity(espacioOcupacionDTO)));
	}

	@Transactional
	public void update(Long requestedId, EspacioOcupacionDTO espacioOcupacionDTO) {
		espacioOcupacionRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("EspacioOcupacion no encontrada o no válida"));

		estadoRepository.findById(espacioOcupacionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		espacioOcupacionDTO.setId(requestedId);
		espacioOcupacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		espacioOcupacionRepository.save(espacioOcupacionMapper.toEntity(espacioOcupacionDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		espacioOcupacionRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Proveedor no encontrado o no válido"));

		espacioOcupacionRepository.deleteById(requestId);
	}

}
