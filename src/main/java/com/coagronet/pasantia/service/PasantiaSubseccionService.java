package com.coagronet.pasantia.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.pasantia.dto.ProductoItemDTO;
import com.coagronet.pasantia.repository.PasantiaProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasantiaSubseccionService {

    private final PasantiaProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<ProductoItemDTO> getItemsBySubseccionId(Long subSeccionId) {
        return productoRepository.findBySubseccionId(subSeccionId).stream()
                .map(producto -> ProductoItemDTO.builder()
                        .producto(producto.getNombre())
                        .productoIdentificador(producto.getId().getIdentificador())
                        .cantidad(producto.getCantidadEsperada())
                        .build())
                .collect(Collectors.toList());
    }
}
