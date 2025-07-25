package com.coagronet.ocupacion.services;

import com.coagronet.ocupacion.dtos.OcupacionDTO;
import com.coagronet.ocupacion.mappers.OcupacionMapper;
import com.coagronet.ocupacion.repositories.OcupacionRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.evaluacion.repositories.EvaluacionRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoActividad.repositories.TipoActividadRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcupacionService {

	private final UserEmpresaService userEmpresaService;

	private final OcupacionMapper ocupacionMapper;

	private final OcupacionRepository ocupacionRepository;

	private final TipoActividadRepository tipoActividadRepository;

	private final EvaluacionRepository evaluacionRepository;

	private final EstadoRepository estadoRepository;

	public List<OcupacionDTO> findAll() {
		return ocupacionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(ocupacionMapper::toListDTO)
			.collect(Collectors.toList());
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
			.orElseThrow(() -> new BadRequestException("El tipo de actividad no es válido."));

		evaluacionRepository
			.findByIdAndEmpresaId(ocupacionDTO.getEvaluacionId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("La evaluación no es válida."));

		estadoRepository.findById(ocupacionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		ocupacionDTO.setId(null);
		ocupacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return ocupacionMapper.toDTO(ocupacionRepository.save(ocupacionMapper.toEntity(ocupacionDTO)));
	}

	public void update(Long requestedId, OcupacionDTO ocupacionDTO) {
		ocupacionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("La ocupación no fue encontrada."));

		tipoActividadRepository
			.findByIdAndEmpresaId(ocupacionDTO.getTipoActividadId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El tipo de actividad no es válido."));

		evaluacionRepository
			.findByIdAndEmpresaId(ocupacionDTO.getEvaluacionId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("La evaluación no es válida."));

		estadoRepository.findById(ocupacionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		ocupacionDTO.setId(requestedId);
		ocupacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		ocupacionRepository.save(ocupacionMapper.toEntity(ocupacionDTO));
	}

	public void delete(Long id) {
		ocupacionRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("La ocupación no fue encontrada."));

		ocupacionRepository.deleteById(id);
	}

}
