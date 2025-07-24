package com.coagronet.evaluacion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.evaluacion.dtos.EvaluacionDTO;
import com.coagronet.evaluacion.mappers.EvaluacionMapper;
import com.coagronet.evaluacion.repositories.EvaluacionRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

	private final UserEmpresaService userEmpresaService;

	private final EvaluacionMapper evaluacionMapper;

	private final EvaluacionRepository evaluacionRepository;

	private final TipoEvaluacionRepository tipoEvaluacionRepository;

	private final EstadoRepository estadoRepository;

	public List<EvaluacionDTO> findAll() {
		return evaluacionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(evaluacionMapper::toListDTO)
			.collect(Collectors.toList());
	}

	public List<EvaluacionDTO> findAllByTipoEvaluacionId(Long tipoEvaluacionId) {
		return evaluacionRepository
			.findByEmpresaIdAndTipoEvaluacionIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(),
					tipoEvaluacionId)
			.stream()
			.map(evaluacionMapper::toListDTO)
			.collect(Collectors.toList());
	}

	public Optional<EvaluacionDTO> findById(Long requestedId) {
		return evaluacionRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(evaluacionMapper::toListDTO);
	}

	public EvaluacionDTO create(EvaluacionDTO evaluacionDTO) {
		tipoEvaluacionRepository.findById(evaluacionDTO.getTipoEvaluacionId())
			.orElseThrow(() -> new BadRequestException("El campo 'tipoEvaluacionId' no es válido."));

		estadoRepository.findById(evaluacionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		evaluacionDTO.setId(null);
		evaluacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return evaluacionMapper.toDTO(evaluacionRepository.save(evaluacionMapper.toEntity(evaluacionDTO)));
	}

	public void update(Long requestedId, EvaluacionDTO evaluacionDTO) {
		evaluacionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(
					() -> new NotFoundException("La evaluación con el ID: " + requestedId + " no fue encontrada."));

		tipoEvaluacionRepository.findById(evaluacionDTO.getTipoEvaluacionId())
			.orElseThrow(() -> new BadRequestException("El campo 'tipoEvaluacionId' no es válido."));

		estadoRepository.findById(evaluacionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El campo 'EstadoId' no es válido."));

		evaluacionDTO.setId(requestedId);
		evaluacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		evaluacionRepository.save(evaluacionMapper.toEntity(evaluacionDTO));
	}

	public void delete(Long id) {
		evaluacionRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("La evaluación con el ID: " + id + " no fue encontrada."));

		evaluacionRepository.deleteById(id);
	}

}
