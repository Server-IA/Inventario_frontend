package com.coagronet.evaluacion.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.evaluacion.dtos.EvaluacionDTO;
import com.coagronet.evaluacion.mappers.EvaluacionMapper;
import com.coagronet.evaluacion.repositories.EvaluacionRepository;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final EmpresaRepository empresaRepository;
    private final EstadoRepository estadoRepository;
    private final EvaluacionMapper evaluacionMapper;

    public List<EvaluacionDTO> findAll(Integer tipoEvaluacionId) {
        return evaluacionRepository.findByTipoEvaluacionId(tipoEvaluacionId).stream()
                .map(evaluacionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
