package com.coagronet.bloque.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
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
import com.coagronet.tipoBloque.repositories.TipoBloqueRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UriBuilderUtil;
import com.coagronet.utils.UserEmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bloque")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BloqueController {

    private final BloqueRepository bloqueRepository;
    private final BloqueMapper bloqueMapper;
    private final EstadoRepository estadoRepository;
    private final SedeRepository sedeRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;
    private final TipoBloqueRepository tipoBloqueRepository;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/{requestedId}")
    private ResponseEntity<BloqueDTO> findById(@PathVariable Long requestedId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        return bloqueRepository
                .findByIdAndSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2L)
                .map(bloqueMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sede/{sedeId}")
    private ResponseEntity<List<BloqueDTO>> findAllBySedeId(
            @PathVariable Long sedeId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        List<BloqueDTO> bloqueDTOs = bloqueRepository
                .findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(sedeId, 2L, empresa.getId())
                .stream()
                .map(bloqueMapper::toDTO)
                .collect(Collectors.toList());

        return !bloqueDTOs.isEmpty()
                ? ResponseEntity.ok(bloqueDTOs)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/minimal/sede/{sedeId}")
    private ResponseEntity<List<BloqueDTO>> findAllMinimalBySedeId(@PathVariable Long sedeId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        List<BloqueDTO> bloqueMinimalDTOs = bloqueRepository
                .findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(sedeId, 2L, empresa.getId())
                .stream()
                .map(bloqueMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return !bloqueMinimalDTOs.isEmpty()
                ? ResponseEntity.ok(bloqueMinimalDTOs)
                : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> createBloque(@Valid @RequestBody BloqueDTO newBloqueRequest, UriComponentsBuilder ucb) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        if (!sedeRepository.existsByIdAndEmpresaIdAndEstadoIdNot(newBloqueRequest.getSedeId(), empresa.getId(), 2)) {
            return ResponseEntity.badRequest().body("La sede no es válida o está inactiva.");
        }

        if (!tipoBloqueRepository.existsByIdAndEmpresaIdAndEstadoIdNot(newBloqueRequest.getTipoBloqueId(),
                empresa.getId(), 2)) {
            return ResponseEntity.badRequest().body("El tipo de bloque no es válido o está inactivo.");
        }

        newBloqueRequest.setId(null);
        newBloqueRequest.setEmpresaId(empresa.getId());

        Bloque savedBloque = bloqueRepository.save(bloqueMapper.toEntity(newBloqueRequest));

        URI locationOfNewBloque = uriBuilderUtil.buildBloqueUri(
                savedBloque.getId(),
                ucb);
        return ResponseEntity.created(locationOfNewBloque).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<?> putBloque(@PathVariable Long requestedId,
            @RequestBody BloqueDTO bloqueUpdate) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        if (!sedeRepository.existsByIdAndEmpresaIdAndEstadoIdNot(bloqueUpdate.getSedeId(), empresa.getId(), 2)) {
            return ResponseEntity.badRequest().body("La sede no es válida o está inactiva.");
        }

        if (!tipoBloqueRepository.existsByIdAndEmpresaIdAndEstadoIdNot(bloqueUpdate.getTipoBloqueId(),
                empresa.getId(), 2)) {
            return ResponseEntity.badRequest().body("El tipo de bloque no es válido o está inactivo.");
        }

        if (bloqueRepository.existsByIdAndSedeEmpresaId(requestedId, empresa.getId())) {
            bloqueUpdate.setId(requestedId);
            bloqueUpdate.setEmpresaId(empresa.getId());

            Bloque bloque = bloqueMapper.toEntity(bloqueUpdate);

            bloqueRepository.save(bloque);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteBloque(@PathVariable Long id) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        Optional<Bloque> bloqueOptional = bloqueRepository.findByIdAndSedeEmpresaId(id, empresa.getId());

        if (bloqueOptional.isPresent()) {
            bloqueRepository.delete(bloqueOptional.get());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
