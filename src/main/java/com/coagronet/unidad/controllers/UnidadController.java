package com.coagronet.unidad.controllers;

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
import com.coagronet.unidad.Unidad;
import com.coagronet.unidad.dtos.UnidadDTO;
import com.coagronet.unidad.mappers.UnidadMapper;
import com.coagronet.unidad.repositories.UnidadRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/unidad")
@CrossOrigin(origins = "*")
public class UnidadController {

    private final UnidadRepository unidadRepository;
    private final UnidadMapper unidadMapper;
    private final EstadoRepository estadoRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    private UnidadController(
            UnidadRepository unidadRepository,
            UnidadMapper unidadMapper,
            EstadoRepository estadoRepository,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository) {
        this.unidadRepository = unidadRepository;
        this.unidadMapper = unidadMapper;
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
    private ResponseEntity<UnidadDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return unidadRepository.findByIdAndEmpresaIdAndEstadoIdNot(
                requestedId,
                empresa.getId(),
                2)
                .map(unidadMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createUnidad(@RequestBody UnidadDTO newUnidadRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        UnidadDTO newUnidad = new UnidadDTO(
                null,
                newUnidadRequest.getNombre(),
                newUnidadRequest.getDescripcion(),
                newUnidadRequest.getEstado(),
                empresa.getId());
        Unidad savedUnidad = unidadMapper.toEntity(newUnidad);
        unidadRepository.save(savedUnidad);
        URI locationOfNewUnidad = ucb
                .path("/api/v1/unidad/{id}")
                .buildAndExpand(savedUnidad.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewUnidad).build();
    }

    @GetMapping
    private ResponseEntity<List<UnidadDTO>> findAll() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<UnidadDTO> unidadDTOs = unidadRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(unidadMapper::toDTO)
                .collect(Collectors.toList());

        return unidadDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(unidadDTOs);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putUnidad(@PathVariable Integer requestedId, @RequestBody UnidadDTO unidadDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Unidad unidad = unidadRepository.findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != unidad) {
            UnidadDTO updateUnidadDTO = new UnidadDTO(
                    requestedId,
                    unidadDTOUpdate.getNombre(),
                    unidadDTOUpdate.getDescripcion(),
                    unidadDTOUpdate.getEstado(),
                    empresa.getId());
            Unidad updatedUnidad = unidadMapper.toEntity(updateUnidadDTO);
            unidadRepository.save(updatedUnidad);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteUnidad(@PathVariable Integer id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (unidadRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            Unidad unidad = unidadRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            unidad.setEstado(estadoInactivo);
            unidadRepository.save(unidad);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
