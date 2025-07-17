package com.coagronet.tipoEvaluacion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
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
                return tipoEvaluacionRepository.findAllByOrderByIdAsc().stream()
                                .map(tipoEvaluacionMapper::toDTO)
                                .collect(Collectors.toList());
        }

        public Optional<TipoEvaluacionDTO> findById(Long requestedId) {
                return tipoEvaluacionRepository.findById(requestedId)
                                .map(tipoEvaluacionMapper::toDTO);
        }

        public TipoEvaluacionDTO create(TipoEvaluacionDTO tipoEvaluacionDTO) {
                estadoRepository.findById(tipoEvaluacionDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El campo estadoId no es válido."));

                tipoEvaluacionDTO.setId(null);

                return tipoEvaluacionMapper.toDTO(
                                tipoEvaluacionRepository.save(
                                                tipoEvaluacionMapper.toEntity(tipoEvaluacionDTO)));
        }

        public void update(Long requestedId, TipoEvaluacionDTO tipoEvaluacionDTO) {
                tipoEvaluacionRepository.findById(requestedId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                estadoRepository.findById(tipoEvaluacionDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El campo estadoId no es válido."));

                tipoEvaluacionDTO.setId(requestedId);

                tipoEvaluacionMapper.toDTO(
                                tipoEvaluacionRepository.save(
                                                tipoEvaluacionMapper.toEntity(tipoEvaluacionDTO)));

        }

        public void delete(Long id) {
                tipoEvaluacionRepository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                tipoEvaluacionRepository.deleteById(id);

        }

}
