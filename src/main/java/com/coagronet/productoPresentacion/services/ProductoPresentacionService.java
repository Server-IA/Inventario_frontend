package com.coagronet.productoPresentacion.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.productoPresentacion.ProductoPresentacion;
import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;
import com.coagronet.productoPresentacion.mappers.ProductoPresentacionMapper;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
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
public class ProductoPresentacionService {

    private final ProductoPresentacionRepository productoPresentacionRepository;
    private final EstadoRepository estadoRepository;
    private final ProductoPresentacionMapper productoPresentacionMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;


    public List<ProductoPresentacionDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();
        return productoPresentacionRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(productoPresentacionMapper::toDto).collect(Collectors.toList());
    }

    public List<ProductoPresentacionDTO> findAllMinimal(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();
        return productoPresentacionRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(productoPresentacionMapper::toMinimalDTO).collect(Collectors.toList());
    }

    public Optional<ProductoPresentacionDTO> findById(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return productoPresentacionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .map(productoPresentacionMapper::toDto);
    }

    @Transactional
    public ProductoPresentacionDTO create(ProductoPresentacionDTO productoPresentacionDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        productoPresentacionDTO.setEmpresaId(empresaId);
        ProductoPresentacion productoPresentacion = productoPresentacionMapper.toEntity(productoPresentacionDTO);
        productoPresentacion = productoPresentacionRepository.save(productoPresentacion);
        return productoPresentacionMapper.toDto(productoPresentacion);
    }

    @Transactional
    public void update(Long requestId, ProductoPresentacionDTO productoPresentacionDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        productoPresentacionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Producto Presentacion no encontrado"));

        estadoRepository.findById(productoPresentacionDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado"));

        productoPresentacionDTO.setId(requestId);
        productoPresentacionDTO.setEmpresaId(empresaId);

        productoPresentacionRepository.save(productoPresentacionMapper.toEntity(productoPresentacionDTO));

    }

    @Transactional
    public void delete(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        productoPresentacionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Producto Presentacion no encontrada"));

        productoPresentacionRepository.deleteById(requestId);
    }

}
