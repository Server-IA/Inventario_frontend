package com.coagronet.sede.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.coagronet.sede.Sede;
import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.dtos.SedeMinimalDTO;
import com.coagronet.sede.mappers.SedeMapper;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/sede")
@CrossOrigin(origins = "*")
public class SedeController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    private final SedeRepository sedeRepository;
    private final SedeMapper sedeMapper;
    private final EstadoRepository estadoRepository;

    public SedeController(
            SedeRepository sedeRepository,
            SedeMapper sedeMapper,
            EstadoRepository estadoRepository) {
        this.sedeRepository = sedeRepository;
        this.sedeMapper = sedeMapper;
        this.estadoRepository = estadoRepository;
    }

    private Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user).stream().map(UserRole::getEmpresa).findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada para el usuario"));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<SedeDTO> findById(@PathVariable Long requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return sedeRepository
                .findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .map(sedeMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createSede(@RequestBody SedeDTO newSedeRequest, UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Estado estado = estadoRepository.findById(newSedeRequest.getEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Sede sede = sedeMapper.toEntity(newSedeRequest);
        sede.setEstado(estado);
        sede.setEmpresa(empresa);
        sedeRepository.save(sede);

        URI locationOfNewSede = ucb
                .path("/api/v1/sede/{id}")
                .buildAndExpand(sede.getId())
                .toUri();

        return ResponseEntity.created(locationOfNewSede).build();
    }

    @GetMapping
    private ResponseEntity<List<SedeDTO>> findAll() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        List<SedeDTO> sedeDTOs = sedeRepository
                .findByEmpresaIdAndEstadoIdNot(empresa.getId(), 2)
                .stream()
                .map(sedeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sedeDTOs);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putSede(
            @PathVariable Long requestedId, @RequestBody SedeDTO sedeUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (sedeRepository.existsByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)) {
            Estado estado = estadoRepository.findById(sedeUpdate.getEstado())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            Sede sede = sedeMapper.toEntity(sedeUpdate);
            sede.setId(requestedId);
            sede.setEmpresa(empresa);
            sede.setEstado(estado);
            sedeRepository.save(sede);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteSede(@PathVariable Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (sedeRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            Sede sede = sedeRepository.findByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
            Estado estadoEliminado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado eliminado no encontrado"));
            sede.setEstado(estadoEliminado);
            sedeRepository.save(sede);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<SedeMinimalDTO>> findAllMinimal() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        List<SedeMinimalDTO> sedeMinimalDTOs = sedeRepository
                .findByEmpresaIdAndEstadoIdNot(empresa.getId(), 2)
                .stream()
                .map(sedeMapper::toMinimalDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sedeMinimalDTOs);
    }

    @GetMapping("/minimal/{requestedId}")
    private ResponseEntity<SedeMinimalDTO> findMinimalById(@PathVariable Long requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return sedeRepository
                .findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .map(sedeMapper::toMinimalDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
