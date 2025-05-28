package com.coagronet.producto.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.producto.Producto;
import com.coagronet.producto.dtos.ProductoDTO;
import com.coagronet.producto.mappers.ProductoMapper;
import com.coagronet.producto.repositories.ProductoRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final EstadoRepository estadoRepository;
    private final ProductoMapper productoMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;


    public List<ProductoDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return productoRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(productoMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductoDTO> findById(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return productoRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(productoMapper::toDto);
    }

    @Transactional
    public ProductoDTO create(ProductoDTO productoDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        estadoRepository.findById(productoDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("Estado no encontrado o no válido"));

        productoDTO.setEmpresaId(empresaId);

        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDto(producto);

    }

    @Transactional
    public void update(Long requestedId, ProductoDTO productoDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();


        productoRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()->new NotFoundException("Producto no encontrado o no válido"));

        estadoRepository.findById(productoDTO.getEstadoId())
                .orElseThrow(()-> new NotFoundException("Estado no encontrado o no válido"));


        productoDTO.setId(requestedId);
        productoDTO.setEmpresaId(empresaId);
        productoRepository.save(productoMapper.toEntity(productoDTO));
    }

    @Transactional
    public void delete(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        productoRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Producto no encontrado o no válido"));

        productoRepository.deleteById(requestId);
    }

}
