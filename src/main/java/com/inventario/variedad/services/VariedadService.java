package com.inventario.variedad.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.utils.UserEmpresaService;
import com.inventario.variedad.mappers.VariedadMapper;
import com.inventario.variedad.repositories.VariedadRepository;

import com.inventario.variedad.dtos.VariedadDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VariedadService {

    private final VariedadRepository variedadRepository;
    private final VariedadMapper variedadMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<VariedadDTO> findAll() {
        return variedadRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(variedadMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<VariedadDTO> findById(Long requestedId) {
        return variedadRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(variedadMapper::toDto);
    }

    @Transactional
    public VariedadDTO create(VariedadDTO variedadDTO) {
        estadoRepository.findById(variedadDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        variedadDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return variedadMapper.toDto(variedadRepository.save(variedadMapper.toEntity(variedadDTO)));
    }

    @Transactional
    public void update(Long requestedId, VariedadDTO variedadDTO) {
        variedadRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Variedad no encontrada o no válida."));

        estadoRepository.findById(variedadDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        variedadDTO.setId(requestedId);
        variedadDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        variedadRepository.save(variedadMapper.toEntity(variedadDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        variedadRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Variedad no encontrada o no válida."));
        variedadRepository.deleteById((requestId));
    }

}
