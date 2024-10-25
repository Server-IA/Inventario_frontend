package com.coagronet.sede.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.empresa.Empresa;
import com.coagronet.sede.Sede;
import com.coagronet.sede.dtos.DatosListadoSede;
import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.mappers.SedeMapper;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/sede")
@CrossOrigin(origins = "*")
public class SedeController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SedeMapper sedeMapper;

    private final SedeRepository sedeRepository;

    private final PagedResourcesAssembler<SedeDTO> pagedResourcesAssembler;

    public SedeController(SedeRepository sedeRepository, PagedResourcesAssembler<SedeDTO> pagedResourcesAssembler) {
        this.sedeRepository = sedeRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    private Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user).stream().map(UserRole::getEmpresa).findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada para el usuario"));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @GetMapping("/short")
    public ResponseEntity<List<DatosListadoSede>> listadoSedes() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Sort sort = Sort.by(Sort.Direction.ASC, "nombre");
        List<Sede> sedes = sedeRepository.findByEstadoNotAndEmpresa(2, empresa, sort);
        List<DatosListadoSede> datosListadoSedes = sedes.stream().map(DatosListadoSede::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(datosListadoSedes);
    }

    @GetMapping
    private ResponseEntity<PagedModel<EntityModel<SedeDTO>>> findAll(Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<Sede> sedePage = sedeRepository.findByEstadoNotAndEmpresa(2, empresa, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        Page<SedeDTO> sedeDTOPage = sedePage.map(sedeMapper::toDto);

        PagedModel<EntityModel<SedeDTO>> pagedModel = pagedResourcesAssembler.toModel(sedeDTOPage);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<SedeDTO> findById(@PathVariable Long requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return sedeRepository.findByIdAndEstadoNotAndEmpresa(requestedId, 2, empresa).map(sedeMapper::toDto)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); // Corrección aquí
    }

}
