package com.coagronet.unidad.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.unidad.Unidad;
import com.coagronet.unidad.dtos.UnidadDTO;
import com.coagronet.unidad.mappers.UnidadMapper;
import com.coagronet.unidad.repositories.UnidadRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UnidadService {

    private final UnidadRepository unidadRepository;
    private final UnidadMapper unidadMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;
    private final EstadoRepository estadoRepository;


    public List<UnidadDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();
        return unidadRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(unidadMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UnidadDTO>findById(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return unidadRepository.findByIdAndEmpresaId(requestId, empresaId)
                .map(unidadMapper::toDTO);
    }

    @Transactional
    public UnidadDTO create(UnidadDTO unidadDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        unidadDTO.setEmpresaId(empresaId);
        Unidad unidad = unidadMapper.toEntity(unidadDTO);
        unidad = unidadRepository.save(unidad);
        return unidadMapper.toDTO(unidad);
    }

    @Transactional
    public void update(Long requestId, UnidadDTO unidadDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        unidadRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Unidad no encontrada"));

        estadoRepository.findById(unidadDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado"));

        unidadDTO.setId(requestId);
        unidadDTO.setEmpresaId(empresaId);
        unidadRepository.save(unidadMapper.toEntity(unidadDTO));
    }

    @Transactional
    public void delete(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        unidadRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Unidad no encontrada"));

        unidadRepository.deleteById(requestId);
    }

}
