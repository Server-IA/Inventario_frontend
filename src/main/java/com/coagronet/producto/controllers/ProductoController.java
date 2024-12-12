package com.coagronet.producto.controllers;

import java.net.URI;

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
import com.coagronet.producto.Producto;
import com.coagronet.producto.dtos.ProductoDTO;
import com.coagronet.producto.dtos.ProductoMinimalDTO;
import com.coagronet.producto.mappers.ProductoMapper;
import com.coagronet.producto.repositories.ProductoRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/producto")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    private ProductoController(
            ProductoRepository productoRepository,
            ProductoMapper productoMapper,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
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
    private ResponseEntity<ProductoDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return productoRepository
                .findByIdAndEmpresaId(requestedId, empresa.getId())
                .map(productoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO newProductoRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        ProductoDTO newProducto = new ProductoDTO(
                null,
                newProductoRequest.getNombre(),
                newProductoRequest.getProductoCategoria(),
                newProductoRequest.getDescripcion(),
                newProductoRequest.getEstado(),
                empresa.getId());
        Producto savedProducto = productoMapper.toEntity(newProducto);
        productoRepository.save(savedProducto);
        URI locationOfNewProducto = ucb
                .path("/api/v1/producto/{id}")
                .buildAndExpand(savedProducto.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewProducto).build();
    }

    @GetMapping
    private ResponseEntity<Page<ProductoDTO>> findAll(@PageableDefault Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<ProductoDTO> page = productoRepository
                .findByEmpresaIdAndEstadoIdNot(
                        empresa.getId(), 2, pageable)
                .map(productoMapper::toDto);
        return page.hasContent()
                ? ResponseEntity.ok(page)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/minimal")
    private ResponseEntity<Page<ProductoMinimalDTO>> findAllMinimal(@PageableDefault Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<ProductoMinimalDTO> page = productoRepository
                .findByEmpresaIdAndEstadoIdNot(
                        empresa.getId(), 2, pageable)
                .map(productoMapper::toMinimalDTO);
        return page.hasContent()
                ? ResponseEntity.ok(page)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putProducto(@PathVariable Integer requestedId,
            @RequestBody ProductoDTO productoDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (productoRepository.existsByIdAndEmpresaId(requestedId, empresa.getId())) {
            ProductoDTO updateProductoDTO = new ProductoDTO(
                    requestedId,
                    productoDTOUpdate.getNombre(),
                    productoDTOUpdate.getProductoCategoria(),
                    productoDTOUpdate.getDescripcion(),
                    productoDTOUpdate.getEstado(),
                    empresa.getId());
            Producto updatedProducto = productoMapper.toEntity(updateProductoDTO);
            productoRepository.save(updatedProducto);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            Empresa empresa = getEmpresaFromUser(authenticatedUser);
            if (productoRepository.existsByIdAndEmpresaId(id, empresa.getId())) {
                productoRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
