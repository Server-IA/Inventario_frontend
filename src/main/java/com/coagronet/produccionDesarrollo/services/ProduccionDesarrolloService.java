package com.coagronet.produccionDesarrollo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.produccionDesarrollo.dtos.ProduccionDesarrolloDTO;
import com.coagronet.produccionDesarrollo.mappers.ProduccionDesarrolloMapper;
import com.coagronet.produccionDesarrollo.repositories.ProduccionDesarrolloRepository;
import com.coagronet.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProduccionDesarrolloService {
    private final ProduccionDesarrolloRepository produccionDesarrolloRepository;
    private final ProduccionDesarrolloMapper produccionDesarrolloMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<ProduccionDesarrolloDTO> findAll() {
        return produccionDesarrolloRepository
                .findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(produccionDesarrolloMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProduccionDesarrolloDTO> findById(Long requestedId) {
        return produccionDesarrolloRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(produccionDesarrolloMapper::toDto);
    }

    @Transactional
    public ProduccionDesarrolloDTO create(ProduccionDesarrolloDTO produccionDesarrolloDTO) {
        estadoRepository.findById(produccionDesarrolloDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        produccionDesarrolloDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return produccionDesarrolloMapper.toDto(
                produccionDesarrolloRepository.save(produccionDesarrolloMapper.toEntity(produccionDesarrolloDTO)));
    }

    @Transactional
    public void update(Long requestedId, ProduccionDesarrolloDTO produccionDesarrolloDTO) {
        produccionDesarrolloRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(
                        () -> new NotFoundException("El desarrollo de produccion no se ha encontrado o no es válido."));

        estadoRepository.findById(produccionDesarrolloDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        produccionDesarrolloDTO.setId(requestedId);
        produccionDesarrolloDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        produccionDesarrolloRepository.save(produccionDesarrolloMapper.toEntity(produccionDesarrolloDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        produccionDesarrolloRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException(
                        "EL desarrollo de produccion no se ha encontrado o no es válido."));

        produccionDesarrolloRepository.deleteById(requestId);
    }

}
