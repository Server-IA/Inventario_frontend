package com.coagronet.espacioOcupacion.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.espacioOcupacion.EspacioOcupacion;
import com.coagronet.espacioOcupacion.dtos.EspacioOcupacionDTO;
import com.coagronet.espacioOcupacion.mappers.EspacioOcupacionMapper;
import com.coagronet.espacioOcupacion.repositories.EspacioOcupacionRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
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
public class EspacioOcupacionService {

    private final EspacioOcupacionRepository espacioOcupacionRepository;
    private final EstadoRepository estadoRepository;
    private final EspacioOcupacionMapper espacioOcupacionMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;


    public List<EspacioOcupacionDTO> findAll(){
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return espacioOcupacionRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(espacioOcupacionMapper::toDTO).collect(Collectors.toList());
    }

    public Optional<EspacioOcupacionDTO> findById(Long requestedId){
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return espacioOcupacionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(espacioOcupacionMapper::toDTO);
    }

    @Transactional
    public EspacioOcupacionDTO create(EspacioOcupacionDTO espacioOcupacionDTO){
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        estadoRepository.findById(espacioOcupacionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        espacioOcupacionDTO.setEmpresaId(empresaId);

        EspacioOcupacion espacioOcupacion = espacioOcupacionMapper.toEntity(espacioOcupacionDTO);
        espacioOcupacion = espacioOcupacionRepository.save(espacioOcupacion);
        return espacioOcupacionMapper.toDTO(espacioOcupacion);
    }


    @Transactional
    public void update(Long requestedId, EspacioOcupacionDTO espacioOcupacionDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        espacioOcupacionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("EspacioOcupacion no encontrada o no válida"));

        estadoRepository.findById(espacioOcupacionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        espacioOcupacionDTO.setId(requestedId);
        espacioOcupacionDTO.setEmpresaId(empresaId);
        espacioOcupacionRepository.save(espacioOcupacionMapper.toEntity(espacioOcupacionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        espacioOcupacionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Proveedor no encontrado o no válido"));

        espacioOcupacionRepository.deleteById(requestId);
    }

}
