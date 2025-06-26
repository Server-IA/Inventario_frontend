package com.coagronet.productoPresentacion.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;
import com.coagronet.productoPresentacion.mappers.ProductoPresentacionMapper;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoPresentacionService {

    private final ProductoPresentacionRepository productoPresentacionRepository;
    private final EstadoRepository estadoRepository;
    private final ProductoPresentacionMapper productoPresentacionMapper;
    private final UserEmpresaService userEmpresaService;

    public List<ProductoPresentacionDTO> findAll() {
        return productoPresentacionRepository
                .findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(productoPresentacionMapper::toDto).collect(Collectors.toList());
    }

    public Optional<ProductoPresentacionDTO> findById(Long requestId) {
        return productoPresentacionRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(productoPresentacionMapper::toDto);
    }

    @Transactional
    public ProductoPresentacionDTO create(ProductoPresentacionDTO productoPresentacionDTO) {
        productoPresentacionDTO.setId(null);
        productoPresentacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return productoPresentacionMapper.toDto(
                productoPresentacionRepository.save(productoPresentacionMapper.toEntity(productoPresentacionDTO)));
    }

    @Transactional
    public void update(Long requestId, ProductoPresentacionDTO productoPresentacionDTO) {
        productoPresentacionRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Producto Presentacion no encontrado"));

        estadoRepository.findById(productoPresentacionDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado"));

        productoPresentacionDTO.setId(requestId);
        productoPresentacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        productoPresentacionRepository.save(productoPresentacionMapper.toEntity(productoPresentacionDTO));

    }

    @Transactional
    public void delete(Long requestId) {
        productoPresentacionRepository
                .findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Producto Presentacion no encontrada"));

        productoPresentacionRepository.deleteById(requestId);
    }

}
