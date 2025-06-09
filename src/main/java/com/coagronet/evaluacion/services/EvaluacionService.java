package com.coagronet.evaluacion.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.evaluacion.dtos.EvaluacionDTO;
import com.coagronet.evaluacion.mappers.EvaluacionMapper;
import com.coagronet.evaluacion.repositories.EvaluacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionMapper evaluacionMapper;

    public List<EvaluacionDTO> findAll(Integer tipoEvaluacionId) {
        return evaluacionRepository.findByTipoEvaluacionId(tipoEvaluacionId).stream()
                .map(evaluacionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
