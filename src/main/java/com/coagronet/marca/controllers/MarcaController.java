package com.coagronet.marca.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.marca.Marca;
import com.coagronet.marca.dtos.MarcaDTO;
import com.coagronet.marca.dtos.MarcaMinimalDTO;
import com.coagronet.marca.mappers.MarcaMapper;
import com.coagronet.marca.repositories.MarcaRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/marca")
@CrossOrigin(origins = "*")
public class MarcaController {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;
    private final EstadoRepository estadoRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    private MarcaController(
            MarcaRepository marcaRepository,
            MarcaMapper marcaMapper,
            EstadoRepository estadoRepository,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository) {
        this.marcaRepository = marcaRepository;
        this.marcaMapper = marcaMapper;
        this.estadoRepository = estadoRepository;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
    }

    private Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user).stream()
                .map(UserRole::getEmpresa)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Empresa no encontrada para el usuario"));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<MarcaDTO> findById(@PathVariable Long requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return marcaRepository.findByIdAndEmpresaIdAndEstadoIdNot(
                requestedId,
                empresa.getId(),
                2)
                .map(marcaMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createMarca(@RequestBody MarcaDTO newMarcaRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        MarcaDTO newMarca = new MarcaDTO(
                null,
                newMarcaRequest.getNombre(),
                newMarcaRequest.getDescripcion(),
                newMarcaRequest.getEstado(),
                empresa.getId());
        Marca savedMarca = marcaMapper.toEntity(newMarca);
        marcaRepository.save(savedMarca);
        URI locationOfNewMarca = ucb
                .path("/api/v1/marca/{id}")
                .buildAndExpand(savedMarca.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewMarca).build();
    }

    @GetMapping
    private ResponseEntity<List<MarcaDTO>> findAll() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<MarcaDTO> marcaDTOs = marcaRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(marcaMapper::toDTO)
                .collect(Collectors.toList());

        return marcaDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(marcaDTOs);
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<MarcaMinimalDTO>> findAllMinimal() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<MarcaMinimalDTO> marcaMinimalDTOs = marcaRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(marcaMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return marcaMinimalDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(marcaMinimalDTOs);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putMarca(@PathVariable Long requestedId,
            @RequestBody MarcaDTO marcaDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Marca marca = marcaRepository.findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != marca) {
            MarcaDTO updateMarcaDTO = new MarcaDTO(
                    requestedId,
                    marcaDTOUpdate.getNombre(),
                    marcaDTOUpdate.getDescripcion(),
                    marcaDTOUpdate.getEstado(),
                    empresa.getId());
            Marca updatedMarca = marcaMapper.toEntity(updateMarcaDTO);
            marcaRepository.save(updatedMarca);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteMarca(@PathVariable Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (marcaRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            Marca marca = marcaRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            marca.setEstado(estadoInactivo);
            marcaRepository.save(marca);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
