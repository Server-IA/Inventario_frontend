package com.coagronet.espacioOcupacion.controllers;

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
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.espacioOcupacion.EspacioOcupacion;
import com.coagronet.espacioOcupacion.dtos.EspacioOcupacionDTO;
import com.coagronet.espacioOcupacion.mappers.EspacioOcupacionMapper;
import com.coagronet.espacioOcupacion.repositories.EspacioOcupacionRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/espacio_ocupacion")
@CrossOrigin(origins = "*")
public class EspacioOcupacionController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    private final EspacioOcupacionRepository espacioOcupacionRepository;
    private final EspacioOcupacionMapper espacioOcupacionMapper;
    private final EstadoRepository estadoRepository;
    private final EspacioRepository espacioRepository;

    public EspacioOcupacionController(
            EspacioOcupacionRepository espacioOcupacionRepository,
            EspacioOcupacionMapper espacioOcupacionMapper,
            EstadoRepository estadoRepository,
            EspacioRepository espacioRepository) {
        this.espacioOcupacionRepository = espacioOcupacionRepository;
        this.espacioOcupacionMapper = espacioOcupacionMapper;
        this.estadoRepository = estadoRepository;
        this.espacioRepository = espacioRepository;
    }

    private Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user).stream()
                .map(UserRole::getEmpresa)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada para el usuario"));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<EspacioOcupacionDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return espacioOcupacionRepository
                .findByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .map(espacioOcupacionMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createEspacioOcupacion(@RequestBody EspacioOcupacionDTO newEspacioOcupacionRequest,
            UriComponentsBuilder ucb) {
        EspacioOcupacionDTO newEspacioOcupacion = new EspacioOcupacionDTO(
                null,
                newEspacioOcupacionRequest.getEspacio(),
                newEspacioOcupacionRequest.getActividadOcupacion(),
                newEspacioOcupacionRequest.getFechaInicio(),
                newEspacioOcupacionRequest.getFechaFin(),
                newEspacioOcupacionRequest.getEstado());
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (espacioRepository.existsByIdAndBloqueSedeEmpresaIdAndEstadoIdNot(
                newEspacioOcupacion.getEspacio(),
                empresa.getId(),
                2)) {
            EspacioOcupacion savedEspacioOcupacion = espacioOcupacionMapper.toEntity(newEspacioOcupacion);
            espacioOcupacionRepository.save(savedEspacioOcupacion);
            URI locationOfNewEspacioOcupacion = ucb
                    .path("/api/v1/espacio_ocupacion/{id}")
                    .buildAndExpand(savedEspacioOcupacion.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewEspacioOcupacion).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/espacio/{espacioId}")
    public ResponseEntity<List<EspacioOcupacionDTO>> findAllByEspacioId(
            @PathVariable Integer espacioId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<EspacioOcupacionDTO> espacioOcupacionDTOs = espacioOcupacionRepository
                .findByEspacioIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNotOrderByIdAsc(espacioId, empresa.getId(), 2)
                .stream()
                .map(espacioOcupacionMapper::toDTO)
                .collect(Collectors.toList());

        return !espacioOcupacionDTOs.isEmpty()
                ? ResponseEntity.ok(espacioOcupacionDTOs)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putEspacioOcupacion(@PathVariable Integer requestedId,
            @RequestBody EspacioOcupacionDTO espacioOcupacionDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        EspacioOcupacion espacioOcupacion = espacioOcupacionRepository
                .findByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(
                        requestedId,
                        empresa.getId(),
                        2)
                .orElse(null);
        if (null != espacioOcupacion) {
            EspacioOcupacionDTO updatedEspacioOcupacionDTO = new EspacioOcupacionDTO(
                    requestedId,
                    espacioOcupacionDTOUpdate.getEspacio(),
                    espacioOcupacionDTOUpdate.getActividadOcupacion(),
                    espacioOcupacionDTOUpdate.getFechaInicio(),
                    espacioOcupacionDTOUpdate.getFechaFin(),
                    espacioOcupacionDTOUpdate.getEstado());
            EspacioOcupacion updatedEspacioOcupacion = espacioOcupacionMapper.toEntity(updatedEspacioOcupacionDTO);
            espacioOcupacionRepository.save(updatedEspacioOcupacion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspacioOcupacion(@PathVariable Integer id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (espacioOcupacionRepository.existsByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            EspacioOcupacion espacioOcupacion = espacioOcupacionRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            espacioOcupacion.setEstado(estadoInactivo);
            espacioOcupacionRepository.save(espacioOcupacion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
