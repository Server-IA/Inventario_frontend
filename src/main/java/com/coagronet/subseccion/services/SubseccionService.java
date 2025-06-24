package com.coagronet.subseccion.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.subseccion.mappers.SubseccionMapper;
import com.coagronet.subseccion.repositories.SubseccionRepository;
import com.coagronet.subseccion.dtos.SubseccionDTO;
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
public class SubseccionService {

    private final SubseccionRepository subseccionRepository;
    private final SubseccionMapper subseccionMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;


    public List<SubseccionDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return subseccionRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(subseccionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<SubseccionDTO> findById(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return subseccionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(subseccionMapper::toDTO);
    }


    @Transactional
    public SubseccionDTO create(SubseccionDTO subseccionDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        estadoRepository.findById(subseccionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("Estado no encontrado o no válido"));

        subseccionDTO.setEmpresaId(empresaId);
        Subseccion subseccion = subseccionMapper.toEntity(subseccionDTO);
        subseccion = subseccionRepository.save(subseccion);
        return subseccionMapper.toDTO(subseccion);
    }

    @Transactional
    public void update(Long requestedId, SubseccionDTO subseccionDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        subseccionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Subseccion no encontrada en su empresa"));

        estadoRepository.findById(subseccionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        subseccionDTO.setId(requestedId);
        subseccionDTO.setEmpresaId(empresaId);
        subseccionRepository.save(subseccionMapper.toEntity(subseccionDTO));
    }


    @Transactional
    public void delete(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        subseccionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Subseccion no encontrada en su empresa"));

        subseccionRepository.deleteById(requestedId);
    }

}
