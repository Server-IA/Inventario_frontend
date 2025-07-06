package com.coagronet.criterioEvaluacion.services;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.criterioEvaluacion.dtos.CriterioEvaluacionDTO;
import com.coagronet.criterioEvaluacion.mappers.CriterioEvaluacionMapper;
import com.coagronet.criterioEvaluacion.repositirories.CriterioEvaluacionRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;
import com.coagronet.utils.UserEmpresaService;

@Service
@RequiredArgsConstructor
public class CriterioEvaluacionService {

    private final UserEmpresaService userEmpresaService;
    private final CriterioEvaluacionRepository criterioEvaluacionRepository;
    private final CriterioEvaluacionMapper criterioEvaluacionMapper;
    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final EstadoRepository estadoRepository;

    public List<CriterioEvaluacionDTO> findAll() {
        return criterioEvaluacionRepository
                .findByEmpresaIdOrderByIdAsc(
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream().map(criterioEvaluacionMapper::toListDTO).collect(Collectors.toList());
    }

    public Optional<CriterioEvaluacionDTO> findById(Long requestedId) {
        return criterioEvaluacionRepository
                .findByIdAndEmpresaId(requestedId,
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(criterioEvaluacionMapper::toListDTO);
    }

    public CriterioEvaluacionDTO createCriterioEvaluacion(CriterioEvaluacionDTO criterioEvaluacionDTO) {
        tipoEvaluacionRepository.findById(criterioEvaluacionDTO.getTipoEvaluacionId())
                .orElseThrow(() -> new BadRequestException("El tipo de evaluación no es válido."));

        estadoRepository.findById(criterioEvaluacionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        criterioEvaluacionDTO.setId(null);
        criterioEvaluacionDTO.setEmpresaId(
                userEmpresaService.getEmpresaIdFromCurrentRequest());

        return criterioEvaluacionMapper
                .toDTO(criterioEvaluacionRepository.save(criterioEvaluacionMapper.toEntity(criterioEvaluacionDTO)));
    }

    public void updateCriterioEvaluacion(Long requestedId, CriterioEvaluacionDTO criterioEvaluacionDTO) {
        criterioEvaluacionRepository
                .findByIdAndEmpresaId(requestedId,
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El criterio de evaluación no fue encontrado."));

        tipoEvaluacionRepository.findById(criterioEvaluacionDTO.getTipoEvaluacionId())
                .orElseThrow(() -> new BadRequestException("El tipo de evaluación no es válido."));

        estadoRepository.findById(criterioEvaluacionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        criterioEvaluacionDTO.setId(requestedId);
        criterioEvaluacionDTO.setEmpresaId(
                userEmpresaService.getEmpresaIdFromCurrentRequest());

        criterioEvaluacionRepository.save(criterioEvaluacionMapper.toEntity(criterioEvaluacionDTO));
    }

    public void deleteCriterioEvaluacion(Long id) {
        criterioEvaluacionRepository
                .findByIdAndEmpresaId(id,
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El criterio de evaluación no fue encontrado."));

        criterioEvaluacionRepository.deleteById(id);
    }

}
