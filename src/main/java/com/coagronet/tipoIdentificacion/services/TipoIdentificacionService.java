package com.coagronet.tipoIdentificacion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.tipoIdentificacion.TipoIdentificacion;
import com.coagronet.tipoIdentificacion.dtos.TipoIdentificacionDTO;
import com.coagronet.tipoIdentificacion.mappers.TipoIdentificacionMapper;
import com.coagronet.tipoIdentificacion.repositories.TipoIdentificacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoIdentificacionService {

    private final TipoIdentificacionRepository tipoIdentificacionRepository;
    private final TipoIdentificacionMapper tipoIdentificacionMapper;

    public List<TipoIdentificacionDTO> findAll() {
        return tipoIdentificacionRepository.findAll()
                .stream()
                .map(tipoIdentificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TipoIdentificacionDTO> findAllAvailable() {
        return tipoIdentificacionRepository.findByEstadoIdNotOrderByIdAsc(2L)
                .stream()
                .map(tipoIdentificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TipoIdentificacionDTO> findById(Long requestedId) {
        return tipoIdentificacionRepository.findById(requestedId)
                .map(tipoIdentificacionMapper::toDTO);
    }

    public TipoIdentificacionDTO create(TipoIdentificacionDTO tipoIdentificacionDTO) {
        tipoIdentificacionDTO.setId(null);
        return tipoIdentificacionMapper.toDTO(
                tipoIdentificacionRepository.save(
                        tipoIdentificacionMapper.toEntity(tipoIdentificacionDTO)));
    }

    public boolean update(Long requestedId, TipoIdentificacionDTO tipoIdentificacionDTO) {
        if (tipoIdentificacionRepository.existsById(requestedId)) {
            tipoIdentificacionDTO.setId(requestedId);
            TipoIdentificacion tipoIdentificacion = tipoIdentificacionMapper.toEntity(tipoIdentificacionDTO);
            tipoIdentificacionRepository.save(tipoIdentificacion);
            return true;
        } else {
            return false;
        }
    }

    public boolean delete(Long id) {
        if (tipoIdentificacionRepository.existsById(id)) {
            tipoIdentificacionRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
