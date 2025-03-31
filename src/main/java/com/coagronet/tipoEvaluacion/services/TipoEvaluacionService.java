package com.coagronet.tipoEvaluacion.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import com.coagronet.tipoEvaluacion.mappers.TipoEvaluacionMapper;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoEvaluacionService {

    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final TipoEvaluacionMapper tipoEvaluacionMapper;

    public List<TipoEvaluacionDTO> findAll() {
        return tipoEvaluacionRepository.findAll().stream()
                .map(tipoEvaluacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TipoEvaluacionDTO> findAllAvailable() {
        return tipoEvaluacionRepository.findByEstadoIdNotOrderByIdAsc(2).stream()
                .map(tipoEvaluacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TipoEvaluacionDTO findById(Integer requestedId) {
        return tipoEvaluacionRepository.findById(requestedId)
                .map(tipoEvaluacionMapper::toDTO)
                .orElse(null); // Devuelve null si no se encuentra
    }

    public TipoEvaluacionDTO create(TipoEvaluacionDTO newtipoEvaluacionDTORequest) {
        TipoEvaluacionDTO tipoEvaluacionDTO = new TipoEvaluacionDTO(
                null,
                newtipoEvaluacionDTORequest.getNombre(),
                newtipoEvaluacionDTORequest.getEstadoId());
        return tipoEvaluacionMapper.toDTO(
                tipoEvaluacionRepository.save(
                        tipoEvaluacionMapper.toEntity(tipoEvaluacionDTO)));
    }

    public boolean update(Integer requestedId, TipoEvaluacionDTO tipoEvaluacionUpdate) {
        if (tipoEvaluacionRepository.existsById(requestedId)) {
            TipoEvaluacionDTO updatedTipoEvaluacion = new TipoEvaluacionDTO(
                    requestedId,
                    tipoEvaluacionUpdate.getNombre(),
                    tipoEvaluacionUpdate.getEstadoId());
            tipoEvaluacionRepository.save(tipoEvaluacionMapper.toEntity(updatedTipoEvaluacion));
            return true;
        } else {
            return false;
        }

    }

    public boolean delete(Integer id) {
        if (tipoEvaluacionRepository.existsById(id)) {
            tipoEvaluacionRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
