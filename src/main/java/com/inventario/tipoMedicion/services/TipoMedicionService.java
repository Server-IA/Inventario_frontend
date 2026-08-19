package com.inventario.tipoMedicion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.tipoMedicion.dtos.TipoMedicionDTO;
import com.inventario.tipoMedicion.mappers.TipoMedicionMapper;
import com.inventario.tipoMedicion.repositories.TipoMedicionRepository;
import com.inventario.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoMedicionService {
    private final TipoMedicionMapper tipoMedicionMapper;
    private final TipoMedicionRepository tipoMedicionRepository;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<TipoMedicionDTO> findAll() {
        return tipoMedicionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(tipoMedicionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<TipoMedicionDTO> findById(Long requestedId) {
        return tipoMedicionRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(tipoMedicionMapper::toDto);
    }

    @Transactional
    public TipoMedicionDTO create(TipoMedicionDTO tipoMedicionDTO) {
        estadoRepository.findById(tipoMedicionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        tipoMedicionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        return tipoMedicionMapper.toDto(tipoMedicionRepository.save(tipoMedicionMapper.toEntity(tipoMedicionDTO)));
    }

    @Transactional
    public void update(Long requestedId, TipoMedicionDTO tipoMedicionDTO) {
        tipoMedicionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El tipo de medición no se ha encontrado o no es válido."));

        estadoRepository.findById(tipoMedicionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        tipoMedicionDTO.setId(requestedId);
        tipoMedicionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        tipoMedicionRepository.save(tipoMedicionMapper.toEntity(tipoMedicionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        tipoMedicionRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El tipo de medición no se ha encontrado o no es válido."));
        tipoMedicionRepository.deleteById(requestId);
    }

}
