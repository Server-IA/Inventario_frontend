package com.coagronet.ordenCompra.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;
import com.coagronet.ordenCompra.mappers.OrdenCompraMapper;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.proveedor.repositories.ProveedorRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/orden_compra")
@CrossOrigin(origins = "*")
public class OrdenCompraController {

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraMapper ordenCompraMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final PedidoRepository pedidoRepository;
    private final ProveedorRepository proveedorRepository;

    private OrdenCompraController(
            OrdenCompraRepository ordenCompraRepository,
            OrdenCompraMapper ordenCompraMapper,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository,
            PedidoRepository pedidoRepository,
            ProveedorRepository proveedorRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraMapper = ordenCompraMapper;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.pedidoRepository = pedidoRepository;
        this.proveedorRepository = proveedorRepository;
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
    private ResponseEntity<OrdenCompraDTO> findById(@PathVariable Long requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return ordenCompraRepository
                .findByIdAndPedidoAlmacenSedeEmpresaId(requestedId, empresa.getId())
                .map(ordenCompraMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createOrdenCompra(@RequestBody OrdenCompraDTO newOrdenCompraRequest,
            UriComponentsBuilder ucb) {
        OrdenCompraDTO newOrdenCompra = new OrdenCompraDTO(
                null,
                newOrdenCompraRequest.getFechaHora(),
                newOrdenCompraRequest.getPedido(),
                newOrdenCompraRequest.getProveedor(),
                newOrdenCompraRequest.getDescripcion(),
                newOrdenCompraRequest.getEstado());
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (pedidoRepository.existsByIdAndAlmacenSedeEmpresaIdAndEstadoIdNot(
                newOrdenCompraRequest.getPedido(), empresa.getId(), 2)
                && proveedorRepository.existsByIdAndEmpresaIdAndEstadoIdNot(
                        newOrdenCompraRequest.getProveedor(), empresa.getId(), 2)) {
            OrdenCompra savedOrdenCompra = ordenCompraMapper.toEntity(newOrdenCompra);
            ordenCompraRepository.save(savedOrdenCompra);
            URI locationOfNewOrdenCompra = ucb
                    .path("/api/v1/orden_compra/{id}")
                    .buildAndExpand(savedOrdenCompra.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewOrdenCompra).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/pedidoId/{requestedPedidoId}")
    private ResponseEntity<List<OrdenCompraDTO>> findAllByPedidoId(@PathVariable Integer requestedPedidoId,
            @PageableDefault Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        System.out.println(requestedPedidoId);
        System.out.println(empresa.getId());
        List<OrdenCompraDTO> ordenCompraDTOs = ordenCompraRepository
                .findByPedidoIdAndPedidoAlmacenSedeEmpresaIdAndEstadoIdNotOrderByIdAsc(
                        requestedPedidoId, empresa.getId(), 2)
                .stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());

        return !ordenCompraDTOs.isEmpty()
                ? ResponseEntity.ok(ordenCompraDTOs)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putOrdenCompra(
            @PathVariable Long requestedId, @RequestBody OrdenCompraDTO ordenCompraDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (ordenCompraRepository.existsByIdAndPedidoAlmacenSedeEmpresaId(
                requestedId, empresa.getId())
                && pedidoRepository.existsByIdAndAlmacenSedeEmpresaIdAndEstadoIdNot(
                        ordenCompraDTOUpdate.getPedido(), empresa.getId(), 2)
                && proveedorRepository.existsByIdAndEmpresaIdAndEstadoIdNot(
                        ordenCompraDTOUpdate.getProveedor(), empresa.getId(), 2)) {
            OrdenCompraDTO updateOrdenCompraDTO = new OrdenCompraDTO(
                    requestedId,
                    ordenCompraDTOUpdate.getFechaHora(),
                    ordenCompraDTOUpdate.getPedido(),
                    ordenCompraDTOUpdate.getProveedor(),
                    ordenCompraDTOUpdate.getDescripcion(),
                    ordenCompraDTOUpdate.getEstado());
            OrdenCompra updatedOrdenCompra = ordenCompraMapper.toEntity(updateOrdenCompraDTO);
            ordenCompraRepository.save(updatedOrdenCompra);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteOrdenCompra(@PathVariable Long id) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            Empresa empresa = getEmpresaFromUser(authenticatedUser);
            if (ordenCompraRepository.existsByIdAndPedidoAlmacenSedeEmpresaId(id, empresa.getId())) {
                ordenCompraRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
