package com.coagronet.produccion.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.dtos.DTOProduccion;
import com.coagronet.produccion.dtos.DatosListadoCortoProduccion;
import com.coagronet.produccion.dtos.DatosProduccion;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.produccion.services.ProduccionService;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/producciones")
@CrossOrigin(origins = "*")
public class ProduccionController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProduccionService produccionService;

    @Autowired
    ProduccionRepository produccionRepository;

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

    @GetMapping("/short/{espacioId}")
    public ResponseEntity<List<DatosListadoCortoProduccion>> listadoCortoProducciones(@PathVariable Integer espacioId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        List<Produccion> producciones = produccionService.obtenerProduccionPorEspaciosShort(espacioId, empresa.getId());
        List<DatosListadoCortoProduccion> datosListadoCortoProducciones = producciones.stream()
                .map(DatosListadoCortoProduccion::new).collect(Collectors.toList());
        return ResponseEntity.ok(datosListadoCortoProducciones);
    }

    @GetMapping("/{espacioId}")
    public ResponseEntity<Page<DatosProduccion>> listadoProducciones(@PathVariable Integer espacioId,
            @PageableDefault Pageable paginacion) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<Produccion> producciones = produccionService.obtenerProduccionPorEspaciosLong(espacioId, empresa.getId(),
                paginacion);
        Page<DatosProduccion> datosProducciones = producciones.map(DatosProduccion::new);
        return ResponseEntity.ok(datosProducciones);
    }

    @PostMapping
    public ResponseEntity<Void> crearProduccion(@RequestBody DTOProduccion dtoProduccion, UriComponentsBuilder ucb) {
        Produccion nuevaProduccion = produccionService.guardarProduccion(dtoProduccion);
        URI ubicacionDeNuevaProduccion = ucb
                .path("/api/v1/producciones/{id}")
                .buildAndExpand(nuevaProduccion.getId())
                .toUri();
        return ResponseEntity.created(ubicacionDeNuevaProduccion).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizarProduccion(@PathVariable Integer id,
            @RequestBody DTOProduccion dtoProduccion) {
        dtoProduccion.setId(id);
        produccionService.actualizarProduccion(dtoProduccion);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProduccion(@PathVariable Integer id) {
        produccionService.eliminarProduccion(id);
        return ResponseEntity.noContent().build();
    }

}
