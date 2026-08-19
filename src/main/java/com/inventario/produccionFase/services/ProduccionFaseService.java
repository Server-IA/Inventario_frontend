package com.inventario.produccionFase.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.produccionFase.dtos.ProduccionFaseDTO;
import com.inventario.produccionFase.mappers.ProduccionFaseMapper;
import com.inventario.produccionFase.repositories.ProduccionFaseRepository;
import com.inventario.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProduccionFaseService {

    private final ProduccionFaseRepository produccionFaseRepository;
    private final ProduccionFaseMapper produccionFaseMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<ProduccionFaseDTO> findAll() {
        return produccionFaseRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(produccionFaseMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProduccionFaseDTO> findById(Long requestedId) {
        return produccionFaseRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(produccionFaseMapper::toDto);
    }

    @Transactional
    public ProduccionFaseDTO create(ProduccionFaseDTO produccionFaseDTO) {
        estadoRepository.findById(produccionFaseDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        produccionFaseDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return produccionFaseMapper
                .toDto(produccionFaseRepository.save(produccionFaseMapper.toEntity(produccionFaseDTO)));

    }

    @Transactional
    public void update(Long requestedId, ProduccionFaseDTO produccionFaseDTO) {
        produccionFaseRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("La fase de producción no se ha encontrado o no es válida."));

        estadoRepository.findById(produccionFaseDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        produccionFaseDTO.setId(requestedId);
        produccionFaseDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        produccionFaseRepository.save(produccionFaseMapper.toEntity(produccionFaseDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        produccionFaseRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(
                        () -> new BadRequestException("La fase de producción no se ha encontrado o no es válida."));
        produccionFaseRepository.deleteById(requestId);
    }

}
