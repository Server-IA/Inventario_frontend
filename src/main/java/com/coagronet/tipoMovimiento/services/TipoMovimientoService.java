package com.coagronet.tipoMovimiento.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoDTO;
import com.coagronet.tipoMovimiento.mappers.TipoMovimientoMapper;
import com.coagronet.tipoMovimiento.repositories.TipoMovimientoRepository;
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
public class TipoMovimientoService {

    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final TipoMovimientoMapper tipoMovimientoMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;


    public List<TipoMovimientoDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        return tipoMovimientoRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(tipoMovimientoMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<TipoMovimientoDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return tipoMovimientoRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(tipoMovimientoMapper::toDto);
    }


    @Transactional
    public TipoMovimientoDTO create(TipoMovimientoDTO tipoMovimientoDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        estadoRepository.findById(tipoMovimientoDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        tipoMovimientoDTO.setEmpresaId(empresaId);

        TipoMovimiento tipoMovimiento = tipoMovimientoMapper.toEntity(tipoMovimientoDTO);
        tipoMovimiento = tipoMovimientoRepository.save(tipoMovimiento);
        return tipoMovimientoMapper.toDto(tipoMovimiento);
    }

    @Transactional
    public void update(Long requestedId, TipoMovimientoDTO tipoMovimientoDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        tipoMovimientoRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("TipoMovimiento no encontrada o no válida"));

        estadoRepository.findById(tipoMovimientoDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        tipoMovimientoDTO.setId(requestedId);
        tipoMovimientoDTO.setEmpresaId(empresaId);
        tipoMovimientoRepository.save(tipoMovimientoMapper.toEntity(tipoMovimientoDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        tipoMovimientoRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("TipoMovimiento no encontrado o no válido"));

        tipoMovimientoRepository.deleteById(requestId);
    }

}
