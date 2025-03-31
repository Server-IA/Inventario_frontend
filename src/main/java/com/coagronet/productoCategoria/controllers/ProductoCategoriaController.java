package com.coagronet.productoCategoria.controllers;

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
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.productoCategoria.ProductoCategoria;
import com.coagronet.productoCategoria.dtos.ProductoCategoriaDTO;
import com.coagronet.productoCategoria.mappers.ProductoCategoriaMapper;
import com.coagronet.productoCategoria.repositories.ProductoCategoriaRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/producto_categoria")
@CrossOrigin(origins = "*")
public class ProductoCategoriaController {

    @Autowired
    private ProductoCategoriaRepository productoCategoriaRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private ProductoCategoriaMapper productoCategoriaMapper;

    private Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user)
                .stream()
                .filter(userRole -> userRole.getRole().getName().equals("ROLE_ADMINISTRADOR_EMPRESA"))
                .map(UserRole::getEmpresa)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Empresa no encontrada para el usuario: " + user.getUsername()));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoCategoriaDTO> findById(@PathVariable Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        ProductoCategoria productoCategoria = productoCategoriaRepository.findByIdAndEmpresaAndEstadoNot(id,
                empresa.getId(), 2);
        ProductoCategoriaDTO productoCategoriaDTO = productoCategoriaMapper.toDTO(productoCategoria);
        if (productoCategoria != null) {
            return ResponseEntity.ok(productoCategoriaDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createProductoCategoria(@RequestBody ProductoCategoriaDTO productoCategoriaDTO,
            UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        productoCategoriaDTO.setEmpresa(empresa.getId());
        ProductoCategoria productoCategoria = productoCategoriaMapper.toEntity(productoCategoriaDTO);
        productoCategoriaRepository.save(productoCategoria);
        URI locationOfNewProductoCategoria = ucb.path("/api/v1/producto_categoria/{id}")
                .buildAndExpand(productoCategoria.getId()).toUri();
        return ResponseEntity.created(locationOfNewProductoCategoria).build();
    }

    @GetMapping
    public ResponseEntity<List<ProductoCategoriaDTO>> findAll() {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        List<ProductoCategoriaDTO> productoCategoriaDTOs = productoCategoriaRepository
                .findByEmpresaAndEstadoNot(empresa.getId(), 2).stream().map(ProductoCategoriaMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productoCategoriaDTOs);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putProductoCategoria(@PathVariable Long requestedId,
            @RequestBody ProductoCategoriaDTO productoCategoriaUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        ProductoCategoria productoCategoria = productoCategoriaMapper.toEntity(productoCategoriaUpdate);
        if (null != productoCategoria) {
            productoCategoria.setId(requestedId);
            productoCategoria.setEmpresa(empresa);
            productoCategoriaRepository.save(productoCategoria);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductoCategoria(@PathVariable Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (productoCategoriaRepository.existsByIdAndEmpresaAndEstadoNot(id, empresa.getId(), 2)) {
            ProductoCategoria productoCategoria = productoCategoriaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("ProductoCategoria not found with id: " + id));
            Estado nuevoEstado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));
            productoCategoria.setEstado(nuevoEstado);
            productoCategoriaRepository.save(productoCategoria);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
