package com.coagronet.tipoEspacio.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
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
import com.coagronet.tipoEspacio.TipoEspacio;
import com.coagronet.tipoEspacio.dtos.TipoEspacioDTO;
import com.coagronet.tipoEspacio.dtos.TipoEspacioMinimalDTO;
import com.coagronet.tipoEspacio.mappers.TipoEspacioMapper;
import com.coagronet.tipoEspacio.repositories.TipoEspacioRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/tipo_espacio")
@CrossOrigin(origins = "*")
public class TipoEspacioController {

    private final TipoEspacioRepository tipoEspacioRepository;
    private final TipoEspacioMapper tipoEspacioMapper;
    private final EstadoRepository estadoRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;

    private TipoEspacioController(
            TipoEspacioRepository tipoEspacioRepository,
            TipoEspacioMapper tipoEspacioMapper,
            EstadoRepository estadoRepository,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository, UserEmpresaService userEmpresaService, AuthenticationService authenticationService) {
        this.tipoEspacioRepository = tipoEspacioRepository;
        this.tipoEspacioMapper = tipoEspacioMapper;
        this.estadoRepository = estadoRepository;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.userEmpresaService = userEmpresaService;
        this.authenticationService = authenticationService;
    }



    @GetMapping("/{requestedId}")
    private ResponseEntity<TipoEspacioDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return tipoEspacioRepository.findByIdAndEmpresaIdAndEstadoIdNot(
                requestedId,
                empresa.getId(),
                2)
                .map(tipoEspacioMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createTipoEspacio(@RequestBody TipoEspacioDTO newTipoEspacioRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        TipoEspacioDTO newTipoEspacio = new TipoEspacioDTO(
                null,
                newTipoEspacioRequest.getNombre(),
                newTipoEspacioRequest.getDescripcion(),
                newTipoEspacioRequest.getEstado(),
                empresa.getId());
        TipoEspacio savedTipoEspacio = tipoEspacioMapper.toEntity(newTipoEspacio);
        tipoEspacioRepository.save(savedTipoEspacio);
        URI locationOfNewTipoEspacio = ucb
                .path("/api/v1/tipo_espacio/{id}")
                .buildAndExpand(savedTipoEspacio.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoEspacio).build();
    }

    @GetMapping
    private ResponseEntity<List<TipoEspacioDTO>> findAll() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        List<TipoEspacioDTO> tipoEspacioDTOs = tipoEspacioRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoEspacioMapper::toDTO)
                .collect(Collectors.toList());

        return tipoEspacioDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoEspacioDTOs);
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<TipoEspacioMinimalDTO>> findAllMinimal() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        List<TipoEspacioMinimalDTO> tipoEspacioDTOs = tipoEspacioRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoEspacioMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return tipoEspacioDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoEspacioDTOs);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoEspacio(@PathVariable Integer requestedId,
            @RequestBody TipoEspacioDTO tipoEspacioDTOUpdate) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        TipoEspacio tipoEspacio = tipoEspacioRepository
                .findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != tipoEspacio) {
            TipoEspacioDTO updateTipoEspacioDTO = new TipoEspacioDTO(
                    requestedId,
                    tipoEspacioDTOUpdate.getNombre(),
                    tipoEspacioDTOUpdate.getDescripcion(),
                    tipoEspacioDTOUpdate.getEstado(),
                    empresa.getId());
            TipoEspacio updatedTipoEspacio = tipoEspacioMapper.toEntity(updateTipoEspacioDTO);
            tipoEspacioRepository.save(updatedTipoEspacio);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoEspacio(@PathVariable Integer id) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        if (tipoEspacioRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            TipoEspacio tipoEspacio = tipoEspacioRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            tipoEspacio.setEstado(estadoInactivo);
            tipoEspacioRepository.save(tipoEspacio);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
