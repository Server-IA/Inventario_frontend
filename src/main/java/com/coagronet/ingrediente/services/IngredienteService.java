package com.coagronet.ingrediente.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ingrediente.dtos.IngredienteDTO;
import com.coagronet.ingrediente.mappers.IngredienteMapper;
import com.coagronet.ingrediente.repositories.IngredienteRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final IngredienteMapper ingredienteMapper;
    private final EstadoRepository estadoRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    public List<IngredienteDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        return ingredienteRepository.findByEmpresaIdOrderByIdAsc(empresa.getId())
                .stream()
                .map(ingredienteMapper::toListDto)
                .collect(Collectors.toList());
    }

    public Optional<IngredienteDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        return ingredienteRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
                .map(ingredienteMapper::toListDto);
    }

    public IngredienteDTO create(IngredienteDTO ingredienteDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        estadoRepository.findById(ingredienteDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        ingredienteDTO.setId(null);
        ingredienteDTO.setEmpresaId(empresa.getId());

        return ingredienteMapper.toDTO(ingredienteRepository.save(ingredienteMapper.toEntity(ingredienteDTO)));
    }

    public void update(Long requestedId, IngredienteDTO ingredienteDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        ingredienteRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
                .orElseThrow(() -> new NotFoundException("Ingrediente no encontrado."));

        estadoRepository.findById(ingredienteDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        ingredienteDTO.setId(requestedId);
        ingredienteDTO.setEmpresaId(empresa.getId());

        ingredienteRepository.save(ingredienteMapper.toEntity(ingredienteDTO));
    }

    public void delete(Long id) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

        ingredienteRepository.findByIdAndEmpresaId(id, empresa.getId())
                .orElseThrow(() -> new NotFoundException("Ingrediente no encontrado."));

        ingredienteRepository.deleteById(id);
    }

}
