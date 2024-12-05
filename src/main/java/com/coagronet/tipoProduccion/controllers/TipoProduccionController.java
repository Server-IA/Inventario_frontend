package com.coagronet.tipoProduccion.controllers;

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
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.tipoProduccion.dtos.TipoProduccionDTO;
import com.coagronet.tipoProduccion.dtos.TipoProduccionMinimalDTO;
import com.coagronet.tipoProduccion.mappers.TipoProduccionMapper;
import com.coagronet.tipoProduccion.repositories.TipoProduccionRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/tipo_produccion")
@CrossOrigin(origins = "*")
public class TipoProduccionController {

    private final TipoProduccionRepository tipoProduccionRepository;
    private final TipoProduccionMapper tipoProduccionMapper;
    private final EstadoRepository estadoRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    private TipoProduccionController(
            TipoProduccionRepository tipoProduccionRepository,
            TipoProduccionMapper tipoProduccionMapper,
            EstadoRepository estadoRepository,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository) {
        this.tipoProduccionRepository = tipoProduccionRepository;
        this.tipoProduccionMapper = tipoProduccionMapper;
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
    private ResponseEntity<TipoProduccionDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return tipoProduccionRepository.findByIdAndEmpresaIdAndEstadoIdNot(
                requestedId,
                empresa.getId(),
                2)
                .map(tipoProduccionMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createTipoProduccion(@RequestBody TipoProduccionDTO newTipoProduccionRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        TipoProduccionDTO newTipoProduccion = new TipoProduccionDTO(
                null,
                newTipoProduccionRequest.getNombre(),
                newTipoProduccionRequest.getDescripcion(),
                newTipoProduccionRequest.getEstado(),
                empresa.getId());
        TipoProduccion savedTipoProduccion = tipoProduccionMapper.toEntity(newTipoProduccion);
        tipoProduccionRepository.save(savedTipoProduccion);
        URI locationOfNewTipoProduccion = ucb
                .path("/api/v1/tipo_produccion/{id}")
                .buildAndExpand(savedTipoProduccion.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoProduccion).build();
    }

    @GetMapping
    private ResponseEntity<List<TipoProduccionDTO>> findAll() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<TipoProduccionDTO> tipoProduccionDTOs = tipoProduccionRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoProduccionMapper::toDto)
                .collect(Collectors.toList());

        return tipoProduccionDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoProduccionDTOs);
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<TipoProduccionMinimalDTO>> findAllMinimal() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<TipoProduccionMinimalDTO> tipoProduccionDTOs = tipoProduccionRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoProduccionMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return tipoProduccionDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoProduccionDTOs);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoProduccion(@PathVariable Integer requestedId,
            @RequestBody TipoProduccionDTO tipoProduccionDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        TipoProduccion tipoProduccion = tipoProduccionRepository
                .findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != tipoProduccion) {
            TipoProduccionDTO updateTipoProduccionDTO = new TipoProduccionDTO(
                    requestedId,
                    tipoProduccionDTOUpdate.getNombre(),
                    tipoProduccionDTOUpdate.getDescripcion(),
                    tipoProduccionDTOUpdate.getEstado(),
                    empresa.getId());
            TipoProduccion updatedTipoProduccion = tipoProduccionMapper.toEntity(updateTipoProduccionDTO);
            tipoProduccionRepository.save(updatedTipoProduccion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoProduccion(@PathVariable Integer id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (tipoProduccionRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            TipoProduccion tipoProduccion = tipoProduccionRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            tipoProduccion.setEstado(estadoInactivo);
            tipoProduccionRepository.save(tipoProduccion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
