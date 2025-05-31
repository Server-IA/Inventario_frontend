package com.coagronet.produccion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.coagronet.empresa.Empresa;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.produccion.dtos.ProduccionDTO;
import com.coagronet.produccion.mappers.ProduccionMapper;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.repositories.ProduccionRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProduccionService {

    private final ProduccionRepository produccionRepository;
    private final ProduccionMapper produccionMapper;
    private final EstadoRepository estadoRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    public List<ProduccionDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        return produccionRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(produccionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ProduccionDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return produccionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(produccionMapper::toDto);
    }


    @Transactional
    public ProduccionDTO create(ProduccionDTO produccionDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        estadoRepository.findById(produccionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        produccionDTO.setEmpresaId(empresaId);

        Produccion produccion = produccionMapper.toEntity(produccionDTO);
        produccion = produccionRepository.save(produccion);
        return produccionMapper.toDto(produccion);
    }

    @Transactional
    public void update(Long requestedId, ProduccionDTO produccionDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        produccionRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Produccion no encontrada o no válida"));

        estadoRepository.findById(produccionDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        produccionDTO.setId(requestedId);
        produccionDTO.setEmpresaId(empresaId);
        produccionRepository.save(produccionMapper.toEntity(produccionDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        produccionRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Produccion no encontrado o no válido"));

        produccionRepository.deleteById(requestId);
    }

}
