package com.inventario.subSeccionDispositivo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.subSeccionDispositivo.dtos.SubseccionDispositivoDTO;
import com.inventario.subSeccionDispositivo.mappers.SubseccionDispositivoMapper;
import com.inventario.subSeccionDispositivo.repositories.SubseccionDispositivoRepository;
import com.inventario.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubseccionDispositivoService {

        private final SubseccionDispositivoRepository subseccionDispositivoRepository;
        private final SubseccionDispositivoMapper subseccionDispositivoMapper;
        private final EstadoRepository estadoRepository;
        private final UserEmpresaService userEmpresaService;

        public List<SubseccionDispositivoDTO> findAll() {
                return subseccionDispositivoRepository
                                .findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .stream()
                                .map(subseccionDispositivoMapper::toDto)
                                .collect(Collectors.toList());
        }

        public Optional<SubseccionDispositivoDTO> findById(Long requestedId) {
                return subseccionDispositivoRepository
                                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .map(subseccionDispositivoMapper::toDto);

        }

        @Transactional
        public SubseccionDispositivoDTO create(SubseccionDispositivoDTO subseccionDispositivoDTO) {
                estadoRepository.findById(subseccionDispositivoDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

                subseccionDispositivoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
                return subseccionDispositivoMapper.toDto(subseccionDispositivoRepository
                                .save(subseccionDispositivoMapper.toEntity(subseccionDispositivoDTO)));

        }

        @Transactional
        public void update(Long requestId, SubseccionDispositivoDTO subseccionDispositivoDTO) {
                subseccionDispositivoRepository
                                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new NotFoundException(
                                                "La subsección del dispositivo no se encuentra o no es válida."));

                estadoRepository.findById(subseccionDispositivoDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

                subseccionDispositivoDTO.setId(requestId);

                subseccionDispositivoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

                subseccionDispositivoRepository.save(subseccionDispositivoMapper.toEntity(subseccionDispositivoDTO));
        }

        @Transactional
        public void delete(Long requestId) {
                subseccionDispositivoRepository
                                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new BadRequestException(
                                                "La subsección del dispositivo no se encuentra o no es válida."));

                subseccionDispositivoRepository.deleteById(requestId);
        }

}
