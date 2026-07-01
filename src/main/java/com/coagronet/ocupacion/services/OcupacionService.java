package com.coagronet.ocupacion.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.evaluacion.repositories.EvaluacionRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.ocupacion.dtos.OcupacionDTO;
import com.coagronet.ocupacion.mappers.OcupacionMapper;
import com.coagronet.ocupacion.repositories.OcupacionRepository;
import com.coagronet.tipoActividad.repositories.TipoActividadRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OcupacionService {

	private final UserEmpresaService userEmpresaService;

	private final OcupacionMapper ocupacionMapper;

	private final OcupacionRepository ocupacionRepository;

	private final TipoActividadRepository tipoActividadRepository;

	private final EvaluacionRepository evaluacionRepository;

	private final EstadoRepository estadoRepository;

	public Page<OcupacionDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return ocupacionRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(ocupacionMapper::toListDTO);
	}

	public Optional<OcupacionDTO> findById(Long requestedId) {
		return ocupacionRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(ocupacionMapper::toListDTO);
	}

	public OcupacionDTO create(OcupacionDTO ocupacionDTO) {
		tipoActividadRepository
				.findByIdAndEmpresaId(ocupacionDTO.getTipoActividadId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El tipo de actividad no es v�lido."));

		evaluacionRepository
				.findByIdAndEmpresaId(ocupacionDTO.getEvaluacionId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("La evaluaci�n no es v�lida."));

		estadoRepository.findById(ocupacionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido."));

		ocupacionDTO.setId(null);
		ocupacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return ocupacionMapper.toDTO(ocupacionRepository.save(ocupacionMapper.toEntity(ocupacionDTO)));
	}

	public void update(Long requestedId, OcupacionDTO ocupacionDTO) {
		ocupacionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("La ocupaci�n no fue encontrada."));

		tipoActividadRepository
				.findByIdAndEmpresaId(ocupacionDTO.getTipoActividadId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El tipo de actividad no es v�lido."));

		evaluacionRepository
				.findByIdAndEmpresaId(ocupacionDTO.getEvaluacionId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("La evaluaci�n no es v�lida."));

		estadoRepository.findById(ocupacionDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido."));

		ocupacionDTO.setId(requestedId);
		ocupacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		ocupacionRepository.save(ocupacionMapper.toEntity(ocupacionDTO));
	}

	public void delete(Long id) {
		ocupacionRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("La ocupaci�n no fue encontrada."));

		ocupacionRepository.deleteById(id);
	}

}
