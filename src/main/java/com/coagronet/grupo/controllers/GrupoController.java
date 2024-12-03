package com.coagronet.grupo.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.empresa.Empresa;
import com.coagronet.grupo.Grupo;
import com.coagronet.grupo.dtos.DatosListadoGrupo;
import com.coagronet.grupo.repositories.GrupoRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/grupo")
@CrossOrigin(origins = "*")
public class GrupoController {

    private final GrupoRepository grupoRepository;

    private GrupoController(GrupoRepository grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

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
    private ResponseEntity<Grupo> findById(@PathVariable Long requestedId) {
        Optional<Grupo> grupoOptional = grupoRepository.findById(requestedId);
        if (grupoOptional.isPresent()) {
            return ResponseEntity.ok(grupoOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/minimal")
    public ResponseEntity<List<DatosListadoGrupo>> listadoGrupos() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Sort sort = Sort.by(Sort.Direction.ASC, "nombre");
        List<Grupo> grupos = grupoRepository.findByEstadoNotAndEmpresa(2, empresa, sort);
        List<DatosListadoGrupo> datosListadoGrupos = grupos.stream().map(DatosListadoGrupo::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(datosListadoGrupos);
    }

    @PostMapping
    private ResponseEntity<Void> createGrupo(@RequestBody Grupo newGrupoRequest, UriComponentsBuilder ucb) {
        Grupo savedGrupo = grupoRepository.save(newGrupoRequest);
        URI locationOfNewGrupo = ucb
                .path("grupo/{id}")
                .buildAndExpand(savedGrupo.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewGrupo).build();
    }

}
