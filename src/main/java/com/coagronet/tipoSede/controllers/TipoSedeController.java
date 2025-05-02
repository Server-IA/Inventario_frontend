package com.coagronet.tipoSede.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import org.springframework.http.ResponseEntity;

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
import com.coagronet.tipoSede.TipoSede;
import com.coagronet.tipoSede.dtos.TipoSedeDTO;
import com.coagronet.tipoSede.dtos.TipoSedeMinimalDTO;
import com.coagronet.tipoSede.mappers.TipoSedeMapper;
import com.coagronet.tipoSede.repositories.TipoSedeRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/tipo_sede")
@CrossOrigin(origins = "*")
public class TipoSedeController {

    private final TipoSedeRepository tipoSedeRepository;
    private final TipoSedeMapper tipoSedeMapper;
    private final EstadoRepository estadoRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    private TipoSedeController(
            TipoSedeRepository tipoSedeRepository,
            TipoSedeMapper tipoSedeMapper,
            EstadoRepository estadoRepository,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository, AuthenticationService authenticationService, UserEmpresaService userEmpresaService) {
        this.tipoSedeRepository = tipoSedeRepository;
        this.tipoSedeMapper = tipoSedeMapper;
        this.estadoRepository = estadoRepository;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.userEmpresaService = userEmpresaService;
    }


    @GetMapping("/{requestedId}")
    private ResponseEntity<TipoSedeDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return tipoSedeRepository.findByIdAndEmpresaIdAndEstadoIdNot(
                requestedId,
                empresa.getId(),
                2)
                .map(tipoSedeMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createTipoSede(@RequestBody TipoSedeDTO newTipoSedeRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        TipoSedeDTO newTipoSede = new TipoSedeDTO(
                null,
                newTipoSedeRequest.getNombre(),
                newTipoSedeRequest.getDescripcion(),
                newTipoSedeRequest.getEstado(),
                empresa.getId());
        TipoSede savedTipoSede = tipoSedeMapper.toEntity(newTipoSede);
        tipoSedeRepository.save(savedTipoSede);
        URI locationOfNewTipoSede = ucb
                .path("/api/v1/tipo_sede/{id}")
                .buildAndExpand(savedTipoSede.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoSede).build();
    }

    @GetMapping
    private ResponseEntity<List<TipoSedeDTO>> findAll() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        List<TipoSedeDTO> tipoSedeDTOs = tipoSedeRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoSedeMapper::toDTO)
                .collect(Collectors.toList());

        return tipoSedeDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoSedeDTOs);
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<TipoSedeMinimalDTO>> findAllMinimal() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        List<TipoSedeMinimalDTO> tipoSedeDTOs = tipoSedeRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                .stream()
                .map(tipoSedeMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return tipoSedeDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoSedeDTOs);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoSede(@PathVariable Integer requestedId,
            @RequestBody TipoSedeDTO tipoSedeDTOUpdate) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        TipoSede tipoSede = tipoSedeRepository.findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != tipoSede) {
            TipoSedeDTO updateTipoSedeDTO = new TipoSedeDTO(
                    requestedId,
                    tipoSedeDTOUpdate.getNombre(),
                    tipoSedeDTOUpdate.getDescripcion(),
                    tipoSedeDTOUpdate.getEstado(),
                    empresa.getId());
            TipoSede updatedTipoSede = tipoSedeMapper.toEntity(updateTipoSedeDTO);
            tipoSedeRepository.save(updatedTipoSede);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoSede(@PathVariable Integer id) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        if (tipoSedeRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            TipoSede tipoSede = tipoSedeRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            tipoSede.setEstado(estadoInactivo);
            tipoSedeRepository.save(tipoSede);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
