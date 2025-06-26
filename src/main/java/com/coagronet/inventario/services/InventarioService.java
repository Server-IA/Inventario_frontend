package com.coagronet.inventario.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.inventario.mappers.InventarioMapper;
import com.coagronet.inventario.repositories.InventarioRepository;
import com.coagronet.inventario.dtos.InventarioDTO;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final EstadoRepository estadoRepository;
    private final InventarioMapper inventarioMapper;
    private final UserEmpresaService userEmpresaService;

    public List<InventarioDTO> findAll() {
        return inventarioRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(inventarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<InventarioDTO> findById(Long requestedId) {
        return inventarioRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(inventarioMapper::toDTO);
    }

    @Transactional
    public InventarioDTO create(InventarioDTO inventarioDTO) {
        estadoRepository.findById(inventarioDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("Estado no encontrado o no válido"));

        inventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return inventarioMapper.toDTO(inventarioRepository.save(inventarioMapper.toEntity(inventarioDTO)));
    }

    @Transactional
    public void update(Long requestedId, InventarioDTO inventarioDTO) {
        inventarioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Inventario no encontrada en su empresa"));

        estadoRepository.findById(inventarioDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        inventarioDTO.setId(requestedId);
        inventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        inventarioRepository.save(inventarioMapper.toEntity(inventarioDTO));
    }

    @Transactional
    public void delete(Long requestedId) {
        inventarioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Inventario no encontrada en su empresa"));

        inventarioRepository.deleteById(requestedId);
    }

}
