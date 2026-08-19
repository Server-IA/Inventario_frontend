package com.inventario.programacion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.programacion.dtos.ProgramacionDTO;
import com.inventario.programacion.mappers.ProgramacionMapper;
import com.inventario.programacion.repositories.ProgramacionRepository;

import com.inventario.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgramacionService {

    private final ProgramacionMapper programacionMapper;
    private final ProgramacionRepository programacionRepository;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<ProgramacionDTO> findAll() {
        return programacionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(programacionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProgramacionDTO> findById(Long requestedId) {
        return programacionRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(programacionMapper::toDto);
    }

    @Transactional
    public ProgramacionDTO create(ProgramacionDTO programacionDTO) {
        estadoRepository.findById(programacionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));
        programacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        return programacionMapper.toDto(programacionRepository.save(programacionMapper.toEntity(programacionDTO)));
    }

    @Transactional
    public void update(Long requestedId, ProgramacionDTO programacionDTO) {
        programacionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El tipo de medición no se ha encontrado o no es válido."));

        estadoRepository.findById(programacionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        programacionDTO.setId(requestedId);
        programacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        programacionRepository.save(programacionMapper.toEntity(programacionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        programacionRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El tipo de medición no se ha encontrado o no es válido."));
        programacionRepository.deleteById(requestId);
    }

}
