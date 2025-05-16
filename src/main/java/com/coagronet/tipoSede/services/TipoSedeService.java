package com.coagronet.tipoSede.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoSede.TipoSede;
import com.coagronet.tipoSede.dtos.TipoSedeDTO;
import com.coagronet.tipoSede.mappers.TipoSedeMapper;
import com.coagronet.tipoSede.repositories.TipoSedeRepository;
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
public class TipoSedeService {

    private final TipoSedeRepository tipoSedeRepository;
    private final TipoSedeMapper tipoSedeMapper;
    private final EstadoRepository estadoRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;


    public List<TipoSedeDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return tipoSedeRepository.findByEmpresaIdOrderByIdAsc(empresa.getId())
                .stream().map(tipoSedeMapper::toListDTO).collect(Collectors.toList());
    }

    public Optional<TipoSedeDTO> findById(Long id){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return tipoSedeRepository.findByIdAndEmpresaId(id, empresa.getId())
                .map(tipoSedeMapper::toListDTO);
    }

    @Transactional
    public TipoSedeDTO create(TipoSedeDTO tipoSedeDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();;

        Estado estado = estadoRepository.findById(tipoSedeDTO.getEstadoId())
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        tipoSedeDTO.setEmpresaId(empresaId);
        TipoSede tipoSede = tipoSedeMapper.toEntity(tipoSedeDTO);
        tipoSede.setEmpresa(empresa);
        tipoSede.setEstado(estado);

        tipoSede = tipoSedeRepository.save(tipoSede);
        return tipoSedeMapper.toDTO(tipoSede);
    }

    @Transactional
    public void update(Long requestedId, TipoSedeDTO tipoSedeDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        tipoSedeRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(() -> new NotFoundException("TipoSede no encontrado"));

        tipoSedeDTO.setId(requestedId);
        tipoSedeDTO.setEmpresaId(empresaId);

        tipoSedeRepository.save(tipoSedeMapper.toEntity(tipoSedeDTO));
    }

    public void delete(Long id){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        tipoSedeRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(()-> new NotFoundException("TipoSede no encontrada"));

        tipoSedeRepository.deleteById(id);
    }

}
