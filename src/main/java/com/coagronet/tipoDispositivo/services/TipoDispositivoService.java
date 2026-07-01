package com.coagronet.tipoDispositivo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.tipoDispositivo.dtos.TipoDispositivoDTO;
import com.coagronet.tipoDispositivo.mappers.TipoDispositivoMapper;
import com.coagronet.tipoDispositivo.repositories.TipoDispositivoRepository;
import com.coagronet.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoDispositivoService {

    private final TipoDispositivoRepository tipoDispositivoRepository;
    private final TipoDispositivoMapper tipoDispositivoMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<TipoDispositivoDTO> findAll() {
        return tipoDispositivoRepository
                .findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(tipoDispositivoMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<TipoDispositivoDTO> findById(Long requestedId) {
        return tipoDispositivoRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(tipoDispositivoMapper::toDto);
    }

    @Transactional
    public TipoDispositivoDTO create(TipoDispositivoDTO tipoDispositivoDTO) {
        estadoRepository.findById(tipoDispositivoDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        tipoDispositivoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        return tipoDispositivoMapper
                .toDto(tipoDispositivoRepository.save(tipoDispositivoMapper.toEntity(tipoDispositivoDTO)));
    }

    @Transactional
    public void update(Long requestId, TipoDispositivoDTO tipoDispositivoDTO) {
        tipoDispositivoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException("Tipo de dispositivo no encontrado o no válido."));

        estadoRepository.findById(tipoDispositivoDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        tipoDispositivoDTO.setId(requestId);
        tipoDispositivoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        tipoDispositivoRepository.save(tipoDispositivoMapper.toEntity(tipoDispositivoDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        tipoDispositivoRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException("Tipo de dispositivo no encontrada o no válida. "));
        tipoDispositivoRepository.deleteById(requestId);
    }
}
