package com.coagronet.grupo.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.coagronet.grupo.dtos.GrupoDTO;
import com.coagronet.grupo.mappers.GrupoMapper;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

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


@RestController
@RequestMapping("/api/v1/grupo")
@CrossOrigin(origins = "*")
public class GrupoController {

    private final GrupoRepository grupoRepository;
    private final GrupoMapper grupoMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    public GrupoController(GrupoRepository grupoRepository, GrupoMapper grupoMapper, AuthenticationService authenticationService, UserEmpresaService userEmpresaService) {
        this.grupoRepository = grupoRepository;
        this.grupoMapper = grupoMapper;
        this.authenticationService = authenticationService;
        this.userEmpresaService = userEmpresaService;
    }


    @GetMapping("/{requestedId}")
    private ResponseEntity<GrupoDTO> findById(@PathVariable Long requestedId) {

        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        return grupoRepository.findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2L)
                .map(grupoMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/v2/{requestedId}")
    private ResponseEntity<GrupoDTO> buscarPorId(@PathVariable Long requestedId) {

        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);

        return grupoRepository.buscarPorIdYEmpresaIdYEstadoEsDiferenteDeInactivo(requestedId, empresa.getId(), 2L)
                .map(grupoMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/minimal")
    public ResponseEntity<List<DatosListadoGrupo>> listadoGrupos() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
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
