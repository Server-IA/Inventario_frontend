package com.coagronet.departamento.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.departamento.Departamento;
import com.coagronet.departamento.dtos.DepartamentoDTO;
import com.coagronet.departamento.mappers.DepartamentoMapper;
import com.coagronet.departamento.repositories.DepartamentoRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.pais.repositories.PaisRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.BadRequestException;
import com.coagronet.utils.NotFoundException;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoMapper departamentoMapper;
    private final DepartamentoRepository departamentoRepository;
    private final PaisRepository paisRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;
    private final EstadoRepository estadoRepository;

    public List<DepartamentoDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        return departamentoRepository.findByEmpresaIdOrderByIdAsc(empresa.getId())
                .stream()
                .map(departamentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DepartamentoDTO> findAllAvailable() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        return departamentoRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L)
                .stream()
                .map(departamentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<DepartamentoDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        return departamentoRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
                .map(departamentoMapper::toDTO);
    }

    public DepartamentoDTO create(DepartamentoDTO departamentoDTO) {

        paisRepository.findByIdAndEmpresaId(departamentoDTO.getPaisId(), departamentoDTO.getEmpresaId())
                .orElseThrow(
                        () -> new BadRequestException("El país no es válido para la empresa del usuario autenticado"));

        estadoRepository.findById(departamentoDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        departamentoDTO.setId(null);
        departamentoDTO.setEmpresaId(empresa.getId());

        return departamentoMapper.toDTO(
                departamentoRepository.save(
                        departamentoMapper.toEntity(departamentoDTO)));
    }

    public void update(Long requestedId, DepartamentoDTO departamentoDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        departamentoRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
                .orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

        paisRepository.findByIdAndEmpresaId(departamentoDTO.getPaisId(), empresa.getId())
                .orElseThrow(
                        () -> new BadRequestException("El país no es válido para la empresa del usuario autenticado"));

        departamentoDTO.setId(requestedId);
        departamentoDTO.setEmpresaId(empresa.getId());

        departamentoRepository.save(
                departamentoMapper.toEntity(departamentoDTO));
    }

    public void delete(Long id) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        departamentoRepository.findByIdAndEmpresaId(id, empresa.getId())
                .orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

        departamentoRepository.deleteById(id);
    }

}
