package com.coagronet.inventarioItem.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.inventarioItem.mappers.InventarioItemMapper;
import com.coagronet.inventarioItem.repositories.InventarioItemRepository;
import com.coagronet.inventarioItem.InventarioItem;
import com.coagronet.inventarioItem.dtos.InventarioItemDTO;
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
public class InventarioItemService {

    private final InventarioItemRepository inventarioItemRepository;
    private final EstadoRepository estadoRepository;
    private final InventarioItemMapper inventarioItemMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;


    public List<InventarioItemDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return inventarioItemRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(inventarioItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<InventarioItemDTO> findById(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return inventarioItemRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(inventarioItemMapper::toDTO);
    }


    @Transactional
    public InventarioItemDTO create(InventarioItemDTO inventarioItemDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        estadoRepository.findById(inventarioItemDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("Estado no encontrado o no válido"));

        inventarioItemDTO.setEmpresaId(empresaId);
        InventarioItem inventarioItem = inventarioItemMapper.toEntity(inventarioItemDTO);
        inventarioItem = inventarioItemRepository.save(inventarioItem);
        return inventarioItemMapper.toDTO(inventarioItem);
    }

    @Transactional
    public void update(Long requestedId, InventarioItemDTO inventarioItemDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        inventarioItemRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("InventarioItem no encontrada en su empresa"));

        estadoRepository.findById(inventarioItemDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        inventarioItemDTO.setId(requestedId);
        inventarioItemDTO.setEmpresaId(empresaId);
        inventarioItemRepository.save(inventarioItemMapper.toEntity(inventarioItemDTO));
    }


    @Transactional
    public void delete(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        inventarioItemRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("InventarioItem no encontrada en su empresa"));

        inventarioItemRepository.deleteById(requestedId);
    }
}
