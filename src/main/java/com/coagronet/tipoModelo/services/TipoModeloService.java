package com.coagronet.tipoModelo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.tipoModelo.dtos.TipoModeloDTO;
import com.coagronet.tipoModelo.mappers.TipoModeloMapper;
import com.coagronet.tipoModelo.repositories.TipoModeloRepository;
import com.coagronet.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoModeloService {
    private final TipoModeloRepository tipoModeloRepository;
    private final TipoModeloMapper tipoModeloMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<TipoModeloDTO> findAll() {
        return tipoModeloRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(tipoModeloMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<TipoModeloDTO> findById(Long requestedId) {
        return tipoModeloRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(tipoModeloMapper::toDto);
    }

    @Transactional
    public TipoModeloDTO create(TipoModeloDTO tipoModeloDTO) {
        estadoRepository.findById(tipoModeloDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));
        tipoModeloDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        return tipoModeloMapper.toDto(tipoModeloRepository.save(tipoModeloMapper.toEntity(tipoModeloDTO)));
    }

    @Transactional
    public void update(Long requestedId, TipoModeloDTO tipoModeloDTO) {
        tipoModeloRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El tipo de modelo no se ha encontrado o no es váldio."));

        estadoRepository.findById(tipoModeloDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        tipoModeloDTO.setId(requestedId);
        tipoModeloDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        tipoModeloRepository.save(tipoModeloMapper.toEntity(tipoModeloDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        tipoModeloRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El tipo de modelo nos e ha encontrado o no es válido."));

        tipoModeloRepository.deleteById(requestId);
    }

}
