package com.coagronet.tipoBloque.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.tipoBloque.TipoBloque;
import com.coagronet.tipoBloque.dtos.TipoBloqueDTO;
import com.coagronet.tipoBloque.mappers.TipoBloqueMapper;
import com.coagronet.tipoBloque.repositories.TipoBloqueRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoBloqueService {
    private final TipoBloqueRepository tipoBloqueRepository;
    private final EstadoRepository estadoRepository;
    private final TipoBloqueMapper tipoBloqueMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;


    public Optional<TipoBloqueDTO> findById(Long requestedId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return tipoBloqueRepository.findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(),2)
                .map(tipoBloqueMapper::toDTO);
    }

    public List<TipoBloqueDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return tipoBloqueRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoBloqueMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TipoBloqueDTO> findAllMinimal(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return tipoBloqueRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoBloqueMapper::toMinimalDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TipoBloqueDTO create(TipoBloqueDTO tipoBloqueDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        tipoBloqueDTO.setEmpresaId(empresa.getId());

        TipoBloque tipoBloque = tipoBloqueMapper.toEntity(tipoBloqueDTO);
        tipoBloque = tipoBloqueRepository.save(tipoBloque);
        return tipoBloqueMapper.toDTO(tipoBloque);
    }

    @Transactional
    public void update(Long requestedId, TipoBloqueDTO tipoBloqueDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

       tipoBloqueRepository.findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElseThrow(() -> new NotFoundException("TipoBloque no encontrado o está inactivo"));

       estadoRepository.findById(tipoBloqueDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado"));


        tipoBloqueDTO.setId(requestedId);
        tipoBloqueDTO.setEmpresaId(empresa.getId());

        tipoBloqueRepository.save(tipoBloqueMapper.toEntity(tipoBloqueDTO));
    }

    @Transactional
    public void delete(Long id){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);


        TipoBloque tipoBloque = tipoBloqueRepository
                .findByIdAndEmpresaId(id, empresa.getId())
                .orElseThrow(() -> new NotFoundException("TipoBloque no encontrado o ya está inactivo"));

        tipoBloqueRepository.deleteById(tipoBloque.getId());

    }



}
