package com.coagronet.marca.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.marca.dtos.MarcaDTO;
import com.coagronet.marca.mappers.MarcaMapper;
import com.coagronet.marca.repositories.MarcaRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;

import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;
    private final EstadoRepository estadoRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    public List<MarcaDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        return marcaRepository.findByEmpresaIdOrderByIdAsc(empresa.getId())
                .stream()
                .map(marcaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<MarcaDTO> findAllAvailable() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        return marcaRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L)
                .stream()
                .map(marcaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<MarcaDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        return marcaRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
                .map(marcaMapper::toDTO);
    }

    public MarcaDTO create(MarcaDTO marcaDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        estadoRepository.findById(marcaDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        marcaDTO.setId(null);
        marcaDTO.setEmpresaId(empresa.getId());

        return marcaMapper.toDTO(marcaRepository.save(marcaMapper.toEntity(marcaDTO)));
    }

    public void update(Long requestedId, MarcaDTO marcaDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        marcaRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
                .orElseThrow(() -> new NotFoundException("Marca no encontrada"));

        estadoRepository.findById(marcaDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        marcaDTO.setId(requestedId);
        marcaDTO.setEmpresaId(empresa.getId());

        marcaRepository.save(marcaMapper.toEntity(marcaDTO));
    }

    public void delete(Long id) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        marcaRepository.findByIdAndEmpresaId(id, empresa.getId())
                .orElseThrow(() -> new NotFoundException("Marca no encontrada"));

        marcaRepository.deleteById(id);
    }

}
