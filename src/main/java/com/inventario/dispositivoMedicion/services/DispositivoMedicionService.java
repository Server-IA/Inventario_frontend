package com.inventario.dispositivoMedicion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.dispositivoMedicion.dtos.DispositivoMedicionDTO;
import com.inventario.dispositivoMedicion.mappers.DispositivoMedicionMapper;
import com.inventario.dispositivoMedicion.repositories.DispositivoMedicionRepository;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DispositivoMedicionService {

    private final DispositivoMedicionRepository dispositivoMedicionRepository;
    private final DispositivoMedicionMapper dispositivoMedicionMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<DispositivoMedicionDTO> findAll() {
        return dispositivoMedicionRepository
                .findByEmpresaIdOrderById(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(dispositivoMedicionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<DispositivoMedicionDTO> findById(Long requestedId) {
        return dispositivoMedicionRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(dispositivoMedicionMapper::toDto);
    }

    @Transactional
    public DispositivoMedicionDTO create(DispositivoMedicionDTO dispositivoMedicionDTO) {
        estadoRepository.findById(dispositivoMedicionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));
        dispositivoMedicionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return dispositivoMedicionMapper
                .toDto(dispositivoMedicionRepository.save(dispositivoMedicionMapper.toEntity(dispositivoMedicionDTO)));
    }

    @Transactional
    public void update(Long requestedId, DispositivoMedicionDTO dispositivoMedicionDTO) {
        dispositivoMedicionRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(
                        () -> new NotFoundException("El dispositivo de medición no se ha encontrado o no es válido."));

        estadoRepository.findById(dispositivoMedicionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        dispositivoMedicionDTO.setId(requestedId);
        dispositivoMedicionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        dispositivoMedicionRepository.save(dispositivoMedicionMapper.toEntity(dispositivoMedicionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        dispositivoMedicionRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException(
                        "El dispositivo de medición no se ha encocntrado o no es válido."));
        dispositivoMedicionRepository.deleteById(requestId);
    }
}
