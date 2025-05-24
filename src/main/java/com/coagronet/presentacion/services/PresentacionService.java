package com.coagronet.presentacion.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.presentacion.Presentacion;
import com.coagronet.presentacion.dtos.PresentacionDTO;
import com.coagronet.presentacion.mappers.PresentacionMapper;
import com.coagronet.presentacion.repositories.PresentacionRepository;
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
public class PresentacionService {

    private final PresentacionRepository presentacionRepository;
    private final PresentacionMapper presentacionMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;
    private final EstadoRepository estadoRepository;


    public List<PresentacionDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return presentacionRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(presentacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PresentacionDTO> findById(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();
        return presentacionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(presentacionMapper::toDTO);
    }

    public List<PresentacionDTO> findAllMinimal(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return presentacionRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(presentacionMapper::toMinimalDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PresentacionDTO create(PresentacionDTO presentacionDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        presentacionDTO.setEmpresaId(empresaId);
        Presentacion presentacion = presentacionMapper.toEntity(presentacionDTO);
        presentacion = presentacionRepository.save(presentacion);
        return presentacionMapper.toDTO(presentacion);
    }

    @Transactional
    public void update(Long requestId, PresentacionDTO presentacionDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        presentacionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Presentacion no encontrada."));

        estadoRepository.findById(presentacionDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado"));

        presentacionDTO.setId(requestId);
        presentacionDTO.setEmpresaId(empresaId);

        presentacionRepository.save(presentacionMapper.toEntity(presentacionDTO));

    }

    @Transactional
    public void delete(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        presentacionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Presentacion no encontrada."));

        presentacionRepository.deleteById(requestId);

    }

}
