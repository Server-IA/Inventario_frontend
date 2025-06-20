package com.coagronet.tipoInventario.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoInventario.TipoInventario;
import com.coagronet.tipoInventario.dtos.TipoInventarioDTO;
import com.coagronet.tipoInventario.mappers.TipoInventarioMapper;
import com.coagronet.tipoInventario.repositories.TipoInventarioRepository;
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
public class TipoInventarioService {

    private final TipoInventarioRepository tipoInventarioRepository;
    private final EstadoRepository estadoRepository;
    private final TipoInventarioMapper tipoInventarioMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;


    public List<TipoInventarioDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return tipoInventarioRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(tipoInventarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TipoInventarioDTO> findById(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return tipoInventarioRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(tipoInventarioMapper::toDTO);
    }


    @Transactional
    public TipoInventarioDTO create(TipoInventarioDTO tipoInventarioDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        estadoRepository.findById(tipoInventarioDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("Estado no encontrado o no válido"));

        tipoInventarioDTO.setEmpresaId(empresaId);
        TipoInventario tipoInventario = tipoInventarioMapper.toEntity(tipoInventarioDTO);
        tipoInventario = tipoInventarioRepository.save(tipoInventario);
        return tipoInventarioMapper.toDTO(tipoInventario);
    }

    @Transactional
    public void update(Long requestedId, TipoInventarioDTO tipoInventarioDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        tipoInventarioRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Tipo inventario no encontrado en su empresa"));

        estadoRepository.findById(tipoInventarioDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        tipoInventarioDTO.setId(requestedId);
        tipoInventarioDTO.setEmpresaId(empresaId);
        tipoInventarioRepository.save(tipoInventarioMapper.toEntity(tipoInventarioDTO));
    }


    @Transactional
    public void delete(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        tipoInventarioRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Tipo inventario no encontrado en su empresa"));

        tipoInventarioRepository.deleteById(requestedId);
    }




}
