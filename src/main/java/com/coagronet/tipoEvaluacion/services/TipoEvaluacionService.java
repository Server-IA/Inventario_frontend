package com.coagronet.tipoEvaluacion.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import com.coagronet.tipoEvaluacion.mappers.TipoEvaluacionMapper;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoEvaluacionService {

    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final TipoEvaluacionMapper tipoEvaluacionMapper;
    private final EstadoRepository estadoRepository;

    public List<TipoEvaluacionDTO> findAll() {
        return tipoEvaluacionRepository.findAll().stream()
                .map(tipoEvaluacionMapper::convert)
                .collect(Collectors.toList());
    }

    public List<TipoEvaluacionDTO> findAllAvailable() {
        return tipoEvaluacionRepository.findByEstadoIdNotOrderByIdAsc(2).stream()
                .map(tipoEvaluacionMapper::convert)
                .collect(Collectors.toList());
    }

    public TipoEvaluacionDTO findById(Integer requestedId) {
        return tipoEvaluacionRepository.findById(requestedId)
                .map(tipoEvaluacionMapper::convert)
                .orElse(null); // Devuelve null si no se encuentra
    }

    public TipoEvaluacionDTO create(TipoEvaluacionDTO newtipoEvaluacionDTORequest) {
        TipoEvaluacionDTO tipoEvaluacionDTO = new TipoEvaluacionDTO(
                null,
                newtipoEvaluacionDTORequest.getNombre(),
                newtipoEvaluacionDTORequest.getEstadoId());
        return tipoEvaluacionMapper.convert(
                tipoEvaluacionRepository.save(
                        tipoEvaluacionMapper.toEntity(tipoEvaluacionDTO)));
    }

    public boolean update(Integer requestedId, TipoEvaluacionDTO tipoEvaluacionUpdate) {
        return tipoEvaluacionRepository.findById(requestedId).map(existingTipoEvaluacion -> {
            existingTipoEvaluacion.setNombre(tipoEvaluacionUpdate.getNombre());

            // Buscar el nuevo Estado en la base de datos
            Estado nuevoEstado = estadoRepository.findById(tipoEvaluacionUpdate.getEstadoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Estado no encontrado con id: " + tipoEvaluacionUpdate.getEstadoId()));

            // Asignar el nuevo Estado
            existingTipoEvaluacion.setEstado(nuevoEstado);

            tipoEvaluacionRepository.save(existingTipoEvaluacion);
            return true;
        }).orElse(false);
    }

    public boolean delete(Integer id) {
        if (tipoEvaluacionRepository.existsById(id)) {
            tipoEvaluacionRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
