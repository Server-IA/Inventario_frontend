package com.coagronet.productoCategoria.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.coagronet.empresa.Empresa;
import com.coagronet.productoCategoria.ProductoCategoria;
import com.coagronet.productoCategoria.dtos.ProductoCategoriaDTO;
import com.coagronet.productoCategoria.mappers.ProductoCategoriaMapper;
import com.coagronet.productoCategoria.repositories.ProductoCategoriaRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/productoCategorias")
@CrossOrigin(origins = "*")
public class ProductoCategoriaController {

    @Autowired
    private ProductoCategoriaRepository productoCategoriaRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductoCategoriaMapper productoCategoriaMapper;

    private Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user)
                .stream()
                .filter(userRole -> userRole.getRole().getName().equals("ROLE_ADMINISTRADOR"))
                .map(UserRole::getEmpresa)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada para el usuario"));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoCategoriaDTO> getProductoCategoria(@PathVariable Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return productoCategoriaRepository.findByIdAndOwner(id, empresa)
                .filter(productoCategoria -> productoCategoria.getEstado() != 2)
                .map(productoCategoriaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createProductoCategoria(@RequestBody ProductoCategoria productoCategoria) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        productoCategoria.setOwner(empresa);
        ProductoCategoria savedProductoCategoria = productoCategoriaRepository.save(productoCategoria);
        URI location = URI.create(String.format("/productoCategorias/%s", savedProductoCategoria.getId()));
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<ProductoCategoriaDTO>> getAllProductoCategorias(Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<ProductoCategoria> page = productoCategoriaRepository.findByEstadoNotAndOwner(0, empresa, pageable);

        List<ProductoCategoriaDTO> dtos = page.getContent().stream()
                .filter(productoCategoria -> productoCategoria.getEstado() != 2)
                .map(productoCategoriaMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProductoCategoria(@PathVariable Long id,
            @RequestBody ProductoCategoria productoCategoria) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Optional<ProductoCategoria> existingProductoCategoria = productoCategoriaRepository.findByIdAndOwner(id,
                empresa);

        if (existingProductoCategoria.isPresent()) {
            productoCategoria.setId(id);
            productoCategoria.setOwner(empresa);
            productoCategoriaRepository.save(productoCategoria);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductoCategoria(@PathVariable Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Optional<ProductoCategoria> existingProductoCategoria = productoCategoriaRepository.findByIdAndOwner(id,
                empresa);

        if (existingProductoCategoria.isPresent()) {
            ProductoCategoria productoCategoria = existingProductoCategoria.get();
            productoCategoria.setEstado(2); // Cambiar el estado a 2 en lugar de eliminar
            productoCategoriaRepository.save(productoCategoria);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
