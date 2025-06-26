package com.coagronet.marca.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.marca.dtos.MarcaDTO;
import com.coagronet.marca.mappers.MarcaMapper;
import com.coagronet.marca.repositories.MarcaRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<MarcaDTO> findAll() {
        return marcaRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(marcaMapper::toListDto)
                .collect(Collectors.toList());
    }

    public Optional<MarcaDTO> findById(Long requestedId) {
        return marcaRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(marcaMapper::toListDto);
    }

    public MarcaDTO create(MarcaDTO marcaDTO) {
        estadoRepository.findById(marcaDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        marcaDTO.setId(null);
        marcaDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return marcaMapper.toDTO(marcaRepository.save(marcaMapper.toEntity(marcaDTO)));
    }

    public void update(Long requestedId, MarcaDTO marcaDTO) {
        marcaRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Marca no encontrada"));

        estadoRepository.findById(marcaDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        marcaDTO.setId(requestedId);
        marcaDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        marcaRepository.save(marcaMapper.toEntity(marcaDTO));
    }

    public void delete(Long id) {
        marcaRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Marca no encontrada"));

        marcaRepository.deleteById(id);
    }

}
