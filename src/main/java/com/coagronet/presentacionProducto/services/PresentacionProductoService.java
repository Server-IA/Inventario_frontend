package com.coagronet.presentacionProducto.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.presentacionProducto.dtos.PresentacionProductoDTO;
import com.coagronet.presentacionProducto.mappers.PresentacionProductoMapper;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresentacionProductoService {

    private final PresentacionProductoRepository presentacionProductoRepository;
    private final EstadoRepository estadoRepository;
    private final PresentacionProductoMapper presentacionProductoMapper;
    private final UserEmpresaService userEmpresaService;

    public List<PresentacionProductoDTO> findAll() {
        return presentacionProductoRepository
                .findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(presentacionProductoMapper::toDto).collect(Collectors.toList());
    }

    public Optional<PresentacionProductoDTO> findById(Long requestId) {
        return presentacionProductoRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(presentacionProductoMapper::toDto);
    }

    @Transactional
    public PresentacionProductoDTO create(PresentacionProductoDTO presentacionProductoDTO) {
        presentacionProductoDTO.setId(null);
        presentacionProductoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return presentacionProductoMapper.toDto(
                presentacionProductoRepository.save(presentacionProductoMapper.toEntity(presentacionProductoDTO)));
    }

    @Transactional
    public void update(Long requestId, PresentacionProductoDTO presentacionProductoDTO) {
        presentacionProductoRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Producto Presentacion no encontrado"));

        estadoRepository.findById(presentacionProductoDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado"));

        presentacionProductoDTO.setId(requestId);
        presentacionProductoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        presentacionProductoRepository.save(presentacionProductoMapper.toEntity(presentacionProductoDTO));

    }

    @Transactional
    public void delete(Long requestId) {
        presentacionProductoRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Producto Presentacion no encontrada"));

        presentacionProductoRepository.deleteById(requestId);
    }

}
