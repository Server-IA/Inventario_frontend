package com.coagronet.bloque.controllers;

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

import com.coagronet.bloque.Bloque;
import com.coagronet.bloque.dtos.BloqueDTO;
import com.coagronet.bloque.dtos.BloqueMinimalDTO;
import com.coagronet.bloque.mappers.BloqueMapper;
import com.coagronet.bloque.repositories.BloqueRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/bloque")
@CrossOrigin(origins = "*")
public class BloqueController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    private final BloqueRepository bloqueRepository;
    private final BloqueMapper bloqueMapper;
    private final EstadoRepository estadoRepository;
    private final SedeRepository sedeRepository;

    private BloqueController(
            BloqueRepository bloqueRepository,
            BloqueMapper bloqueMapper,
            EstadoRepository estadoRepository,
            SedeRepository sedeRepository) {
        this.bloqueRepository = bloqueRepository;
        this.bloqueMapper = bloqueMapper;
        this.estadoRepository = estadoRepository;
        this.sedeRepository = sedeRepository;
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
    private ResponseEntity<BloqueDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return bloqueRepository
                .findByIdAndSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .map(bloqueMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sede/{sedeId}")
    private ResponseEntity<List<BloqueDTO>> findAllBySedeId(
            @PathVariable Long sedeId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<BloqueDTO> bloqueDTOs = bloqueRepository
                .findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(sedeId, 2, empresa.getId())
                .stream()
                .map(bloqueMapper::toDTO)
                .collect(Collectors.toList());

        return !bloqueDTOs.isEmpty()
                ? ResponseEntity.ok(bloqueDTOs)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/minimal/sede/{sedeId}")
    private ResponseEntity<List<BloqueMinimalDTO>> findAllMinimalBySedeId(@PathVariable Long sedeId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<BloqueMinimalDTO> bloqueMinimalDTOs = bloqueRepository
                .findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(sedeId, 2, empresa.getId())
                .stream()
                .map(bloqueMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return !bloqueMinimalDTOs.isEmpty()
                ? ResponseEntity.ok(bloqueMinimalDTOs)
                : ResponseEntity.noContent().build();
    }

    @PostMapping
    private ResponseEntity<Void> createBloque(@RequestBody BloqueDTO newBloqueRequest, UriComponentsBuilder ucb) {
        BloqueDTO newBloque = new BloqueDTO(null, newBloqueRequest.getSede(), newBloqueRequest.getTipoBloque(),
                newBloqueRequest.getNombre(), newBloqueRequest.getGeolocalizacion(), newBloqueRequest.getCoordenadas(),
                newBloqueRequest.getNumeroPisos(), newBloqueRequest.getDescripcion(), newBloqueRequest.getEstado());
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (sedeRepository.existsByIdAndEmpresaIdAndEstadoIdNot(newBloque.getSede(), empresa.getId(), 2)) {
            Bloque savedBloque = bloqueMapper.toEntity(newBloque);
            bloqueRepository.save(savedBloque);
            URI locationOfNewBloque = ucb
                    .path("/api/v1/bloque/{id}")
                    .buildAndExpand(savedBloque.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewBloque).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putBloque(@PathVariable Integer requestedId,
            @RequestBody BloqueDTO bloqueDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Bloque bloque = bloqueRepository.findByIdAndSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElse(null);
        if (null != bloque) {
            BloqueDTO updatedBloqueDTO = new BloqueDTO(requestedId, bloqueDTOUpdate.getSede(),
                    bloqueDTOUpdate.getTipoBloque(), bloqueDTOUpdate.getNombre(), bloqueDTOUpdate.getGeolocalizacion(),
                    bloqueDTOUpdate.getCoordenadas(),
                    bloqueDTOUpdate.getNumeroPisos(), bloqueDTOUpdate.getDescripcion(), bloqueDTOUpdate.getEstado());
            Bloque updatedBloque = bloqueMapper.toEntity(updatedBloqueDTO);
            bloqueRepository.save(updatedBloque);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteBloque(@PathVariable Integer id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (bloqueRepository.existsByIdAndSedeEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            Bloque bloque = bloqueRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            bloque.setEstado(estadoInactivo);
            bloqueRepository.save(bloque);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
