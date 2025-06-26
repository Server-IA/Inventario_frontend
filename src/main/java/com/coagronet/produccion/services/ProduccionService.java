package com.coagronet.produccion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.produccion.dtos.ProduccionDTO;
import com.coagronet.produccion.mappers.ProduccionMapper;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.produccion.repositories.ProduccionRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProduccionService {

    private final ProduccionRepository produccionRepository;
    private final ProduccionMapper produccionMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<ProduccionDTO> findAll() {
        return produccionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(produccionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProduccionDTO> findById(Long requestedId) {
        return produccionRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(produccionMapper::toDto);
    }

    @Transactional
    public ProduccionDTO create(ProduccionDTO produccionDTO) {
        estadoRepository.findById(produccionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        produccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return produccionMapper.toDto(produccionRepository.save(produccionMapper.toEntity(produccionDTO)));
    }

    @Transactional
    public void update(Long requestedId, ProduccionDTO produccionDTO) {
        produccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Produccion no encontrada o no válida"));

        estadoRepository.findById(produccionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        produccionDTO.setId(requestedId);
        produccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        produccionRepository.save(produccionMapper.toEntity(produccionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        produccionRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Produccion no encontrado o no válido"));

        produccionRepository.deleteById(requestId);
    }

}
