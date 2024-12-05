package com.coagronet.tipoBloque.controllers;

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
import com.coagronet.tipoBloque.TipoBloque;
import com.coagronet.tipoBloque.dtos.TipoBloqueDTO;
import com.coagronet.tipoBloque.dtos.TipoBloqueMinimalDTO;
import com.coagronet.tipoBloque.mappers.TipoBloqueMapper;
import com.coagronet.tipoBloque.repositories.TipoBloqueRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/tipo_bloque")
@CrossOrigin(origins = "*")
public class TipoBloqueController {

    private final TipoBloqueRepository tipoBloqueRepository;
    private final TipoBloqueMapper tipoBloqueMapper;
    private final EstadoRepository estadoRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    private TipoBloqueController(
            TipoBloqueRepository tipoBloqueRepository,
            TipoBloqueMapper tipoBloqueMapper,
            EstadoRepository estadoRepository,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository) {
        this.tipoBloqueRepository = tipoBloqueRepository;
        this.tipoBloqueMapper = tipoBloqueMapper;
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
    private ResponseEntity<TipoBloqueDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return tipoBloqueRepository.findByIdAndEmpresaIdAndEstadoIdNot(
                requestedId,
                empresa.getId(),
                2)
                .map(tipoBloqueMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createTipoBloque(@RequestBody TipoBloqueDTO newTipoBloqueRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        TipoBloqueDTO newTipoBloque = new TipoBloqueDTO(
                null,
                newTipoBloqueRequest.getNombre(),
                newTipoBloqueRequest.getDescripcion(),
                newTipoBloqueRequest.getEstado(),
                empresa.getId());
        TipoBloque savedTipoBloque = tipoBloqueMapper.toEntity(newTipoBloque);
        tipoBloqueRepository.save(savedTipoBloque);
        URI locationOfNewTipoBloque = ucb
                .path("/api/v1/tipo_bloque/{id}")
                .buildAndExpand(savedTipoBloque.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoBloque).build();
    }

    @GetMapping
    private ResponseEntity<List<TipoBloqueDTO>> findAll() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<TipoBloqueDTO> tipoBloqueDTOs = tipoBloqueRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoBloqueMapper::toDTO)
                .collect(Collectors.toList());

        return tipoBloqueDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoBloqueDTOs);
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<TipoBloqueMinimalDTO>> findAllMinimal() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<TipoBloqueMinimalDTO> tipoBloqueDTOs = tipoBloqueRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoBloqueMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return tipoBloqueDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoBloqueDTOs);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoBloque(@PathVariable Integer requestedId,
            @RequestBody TipoBloqueDTO tipoBloqueDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        TipoBloque tipoBloque = tipoBloqueRepository.findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != tipoBloque) {
            TipoBloqueDTO updateTipoBloqueDTO = new TipoBloqueDTO(
                    requestedId,
                    tipoBloqueDTOUpdate.getNombre(),
                    tipoBloqueDTOUpdate.getDescripcion(),
                    tipoBloqueDTOUpdate.getEstado(),
                    empresa.getId());
            TipoBloque updatedTipoBloque = tipoBloqueMapper.toEntity(updateTipoBloqueDTO);
            tipoBloqueRepository.save(updatedTipoBloque);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoBloque(@PathVariable Integer id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (tipoBloqueRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            TipoBloque tipoBloque = tipoBloqueRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            tipoBloque.setEstado(estadoInactivo);
            tipoBloqueRepository.save(tipoBloque);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}