package com.coagronet.proveedor.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.proveedor.dtos.ProveedorDTO;
import com.coagronet.proveedor.mappers.ProveedorMapper;
import com.coagronet.proveedor.repositories.ProveedorRepository;
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
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final EstadoRepository estadoRepository;
    private final ProveedorMapper proveedorMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    public List<ProveedorDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        return proveedorRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(proveedorMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProveedorDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return proveedorRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(proveedorMapper::toDto);
    }


    @Transactional
    public ProveedorDTO create(ProveedorDTO produccionDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        estadoRepository.findById(produccionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        produccionDTO.setEmpresaId(empresaId);

        Proveedor produccion = proveedorMapper.toEntity(produccionDTO);
        produccion = proveedorRepository.save(produccion);
        return proveedorMapper.toDto(produccion);
    }

    @Transactional
    public void update(Long requestedId, ProveedorDTO produccionDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        proveedorRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Proveedor no encontrada o no válida"));

        estadoRepository.findById(produccionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        produccionDTO.setId(requestedId);
        produccionDTO.setEmpresaId(empresaId);
        proveedorRepository.save(proveedorMapper.toEntity(produccionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        proveedorRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Proveedor no encontrado o no válido"));

        proveedorRepository.deleteById(requestId);
    }
    
    
}
