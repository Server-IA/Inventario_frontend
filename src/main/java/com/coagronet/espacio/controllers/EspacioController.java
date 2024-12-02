package com.coagronet.espacio.controllers;

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

import com.coagronet.bloque.repositories.BloqueRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
import com.coagronet.espacio.dtos.EspacioDTO;
import com.coagronet.espacio.dtos.EspacioMinimalDTO;
import com.coagronet.espacio.mappers.EspacioMapper;
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/espacio")
@CrossOrigin(origins = "*")
public class EspacioController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    private final EspacioRepository espacioRepository;
    private final EspacioMapper espacioMapper;
    private final EstadoRepository estadoRepository;
    private final BloqueRepository bloqueRepository;

    public EspacioController(
            EspacioRepository espacioRepository,
            EspacioMapper espacioMapper,
            EstadoRepository estadoRepository,
            BloqueRepository bloqueRepository) {
        this.espacioRepository = espacioRepository;
        this.espacioMapper = espacioMapper;
        this.estadoRepository = estadoRepository;
        this.bloqueRepository = bloqueRepository;
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
    public ResponseEntity<EspacioDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return espacioRepository
                .findByIdAndBloqueSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .map(espacioMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bloque/{bloqueId}")
    public ResponseEntity<List<EspacioDTO>> findAllByBloqueId(
            @PathVariable Integer bloqueId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<EspacioDTO> espacioDTOs = espacioRepository
                .findByBloqueSedeEmpresaIdAndBloqueIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), bloqueId, 2)
                .stream()
                .map(espacioMapper::toDTO)
                .collect(Collectors.toList());

        return !espacioDTOs.isEmpty()
                ? ResponseEntity.ok(espacioDTOs)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/minimal/bloque/{bloqueId}")
    public ResponseEntity<List<EspacioMinimalDTO>> findAllMinimalByBloqueId(@PathVariable Integer bloqueId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<EspacioMinimalDTO> espacioMinimalDTOs = espacioRepository
                .findByBloqueSedeEmpresaIdAndBloqueIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), bloqueId, 2)
                .stream()
                .map(espacioMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return !espacioMinimalDTOs.isEmpty()
                ? ResponseEntity.ok(espacioMinimalDTOs)
                : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> createEspacio(@RequestBody EspacioDTO newEspacioRequest, UriComponentsBuilder ucb) {
        EspacioDTO newEspacio = new EspacioDTO(null, newEspacioRequest.getBloque(), newEspacioRequest.getTipoEspacio(),
                newEspacioRequest.getNombre(), newEspacioRequest.getGeolocalizacion(),
                newEspacioRequest.getCoordenadas(), newEspacioRequest.getDescripcion(), newEspacioRequest.getEstado());
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (bloqueRepository.existsByIdAndSedeEmpresaIdAndEstadoIdNot(newEspacio.getBloque(), empresa.getId(), 2)) {
            Espacio savedEspacio = espacioMapper.toEntity(newEspacio);
            espacioRepository.save(savedEspacio);
            URI locationOfNewEspacio = ucb
                    .path("/api/v1/espacio/{id}")
                    .buildAndExpand(savedEspacio.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewEspacio).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putEspacio(@PathVariable Integer requestedId,
            @RequestBody EspacioDTO espacioDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Espacio espacio = espacioRepository
                .findByIdAndBloqueSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != espacio) {
            EspacioDTO updatedEspacioDTO = new EspacioDTO(requestedId, espacioDTOUpdate.getBloque(),
                    espacioDTOUpdate.getTipoEspacio(),
                    espacioDTOUpdate.getNombre(), espacioDTOUpdate.getGeolocalizacion(),
                    espacioDTOUpdate.getCoordenadas(), espacioDTOUpdate.getDescripcion(), espacioDTOUpdate.getEstado());
            Espacio updatedEspacio = espacioMapper.toEntity(updatedEspacioDTO);
            espacioRepository.save(updatedEspacio);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspacio(@PathVariable Integer id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (espacioRepository.existsByIdAndBloqueSedeEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            Espacio espacio = espacioRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            espacio.setEstado(estadoInactivo);
            espacioRepository.save(espacio);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
