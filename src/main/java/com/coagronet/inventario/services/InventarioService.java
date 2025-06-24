package com.coagronet.inventario.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.inventario.mappers.InventarioMapper;
import com.coagronet.inventario.repositories.InventarioRepository;
import com.coagronet.inventario.Inventario;
import com.coagronet.inventario.dtos.InventarioDTO;
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
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final EstadoRepository estadoRepository;
    private final InventarioMapper inventarioMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;



    public List<InventarioDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return inventarioRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(inventarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<InventarioDTO> findById(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return inventarioRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(inventarioMapper::toDTO);
    }


    @Transactional
    public InventarioDTO create(InventarioDTO inventarioDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        estadoRepository.findById(inventarioDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("Estado no encontrado o no válido"));

        inventarioDTO.setEmpresaId(empresaId);
        Inventario inventario = inventarioMapper.toEntity(inventarioDTO);
        inventario = inventarioRepository.save(inventario);
        return inventarioMapper.toDTO(inventario);
    }

    @Transactional
    public void update(Long requestedId, InventarioDTO inventarioDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        inventarioRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Inventario no encontrada en su empresa"));

        estadoRepository.findById(inventarioDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        inventarioDTO.setId(requestedId);
        inventarioDTO.setEmpresaId(empresaId);
        inventarioRepository.save(inventarioMapper.toEntity(inventarioDTO));
    }


    @Transactional
    public void delete(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        inventarioRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Inventario no encontrada en su empresa"));

        inventarioRepository.deleteById(requestedId);
    }



}
