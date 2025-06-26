package com.coagronet.ordenCompra.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;
import com.coagronet.ordenCompra.mappers.OrdenCompraMapper;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraMapper ordenCompraMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<OrdenCompraDTO> findAll() {
        return ordenCompraRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<OrdenCompraDTO> findById(Long requestedId) {
        return ordenCompraRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(ordenCompraMapper::toDTO);
    }

    @Transactional
    public OrdenCompraDTO create(OrdenCompraDTO ordenCompraDTO) {
        estadoRepository.findById(ordenCompraDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        ordenCompraDTO.setId(null);
        ordenCompraDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return ordenCompraMapper.toDTO(ordenCompraRepository.save(ordenCompraMapper.toEntity(ordenCompraDTO)));
    }

    @Transactional
    public void update(Long requestedId, OrdenCompraDTO ordenCompraDTO) {
        ordenCompraRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("OrdenCompra no encontrada o no válida"));

        estadoRepository.findById(ordenCompraDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        ordenCompraDTO.setId(requestedId);
        ordenCompraDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        ordenCompraRepository.save(ordenCompraMapper.toEntity(ordenCompraDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        ordenCompraRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("OrdenCompra no encontrado o no válido"));

        ordenCompraRepository.deleteById(requestId);
    }
}
