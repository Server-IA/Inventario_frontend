package com.coagronet.productoLocalizacion.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.productoLocalizacion.mappers.ProductoLocalizacionMapper;
import com.coagronet.productoLocalizacion.repositories.ProductoLocalizacionRepository;
import com.coagronet.productoLocalizacion.dtos.ProductoLocalizacionDTO;
import com.coagronet.subseccion.repositories.SubseccionRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoLocalizacionService {

    private final ProductoLocalizacionRepository productoLocalizacionRepository;
    private final ProductoLocalizacionMapper productoLocalizacionMapper;
    private final EstadoRepository estadoRepository;
    private final SubseccionRepository subseccionRepository;
    private final UserEmpresaService userEmpresaService;


    public List<ProductoLocalizacionDTO> findAll() {
        return productoLocalizacionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(productoLocalizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductoLocalizacionDTO> findById(Long requestedId) {
        return productoLocalizacionRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(productoLocalizacionMapper::toDto);
    }

    @Transactional
    public ProductoLocalizacionDTO create(ProductoLocalizacionDTO productoLocalizacionDTO) {

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        estadoRepository.findById(productoLocalizacionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        subseccionRepository.findByIdAndEmpresaId(productoLocalizacionDTO.getSubseccionId(), empresaId)
                .orElseThrow(() -> new BadRequestException("La subseccion no es válida para esta empresa"));


        productoLocalizacionDTO.setEmpresaId(empresaId);

        return productoLocalizacionMapper.toDto(productoLocalizacionRepository.save(productoLocalizacionMapper.toEntity(productoLocalizacionDTO)));
    }

    @Transactional
    public void update(Long requestedId, ProductoLocalizacionDTO productoLocalizacionDTO) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        productoLocalizacionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("ProductoLocalizacion no encontrada o no válida"));

        estadoRepository.findById(productoLocalizacionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        productoLocalizacionDTO.setId(requestedId);
        productoLocalizacionDTO.setEmpresaId(empresaId);

        productoLocalizacionRepository.save(productoLocalizacionMapper.toEntity(productoLocalizacionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        productoLocalizacionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(() -> new NotFoundException("ProductoLocalizacion no encontrado o no válido"));

        productoLocalizacionRepository.deleteById(requestId);
    }
}
