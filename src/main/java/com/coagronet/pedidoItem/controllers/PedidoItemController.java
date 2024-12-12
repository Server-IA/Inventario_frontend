package com.coagronet.pedidoItem.controllers;

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
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.pedidoItem.PedidoItem;
import com.coagronet.pedidoItem.dtos.PedidoItemDTO;
import com.coagronet.pedidoItem.mappers.PedidoItemMapper;
import com.coagronet.pedidoItem.repositories.PedidoItemRepository;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/pedido_item")
public class PedidoItemController {

    private final PedidoItemRepository pedidoItemRepository;
    private final PedidoItemMapper pedidoItemMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoPresentacionRepository productoPresentacionRepository;

    private PedidoItemController(
            PedidoItemRepository pedidoItemRepository,
            PedidoItemMapper pedidoItemMapper,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository,
            PedidoRepository pedidoRepository,
            ProductoPresentacionRepository productoPresentacionRepository) {
        this.pedidoItemRepository = pedidoItemRepository;
        this.pedidoItemMapper = pedidoItemMapper;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoPresentacionRepository = productoPresentacionRepository;
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
    private ResponseEntity<PedidoItemDTO> findById(@PathVariable Long requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return pedidoItemRepository
                .findByIdAndPedidoAlmacenSedeEmpresaId(requestedId, empresa.getId())
                .map(pedidoItemMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createPedidoItem(@RequestBody PedidoItemDTO newPedidoItemRequest,
            UriComponentsBuilder ucb) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (pedidoRepository.existsByIdAndAlmacenSedeEmpresaId(
                newPedidoItemRequest.getPedido(), empresa.getId())
                && productoPresentacionRepository.existsByIdAndProductoEmpresaId(
                        newPedidoItemRequest.getProductoPresentacion(), empresa.getId())) {
            PedidoItemDTO newPedidoItem = new PedidoItemDTO(
                    null,
                    newPedidoItemRequest.getPedido(),
                    newPedidoItemRequest.getProductoPresentacion(),
                    newPedidoItemRequest.getCantidad(),
                    newPedidoItemRequest.getEstado());
            PedidoItem savedPedidoItem = pedidoItemMapper.toEntity(newPedidoItem);
            pedidoItemRepository.save(savedPedidoItem);
            URI locationOfNewPedidoItem = ucb
                    .path("/api/v1/pedido_item/{id}")
                    .buildAndExpand(savedPedidoItem.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewPedidoItem).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/pedido/{requestedId}")
    private ResponseEntity<Page<PedidoItemDTO>> findAllByPedidoId(@PathVariable Integer requestedId,
            @PageableDefault Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<PedidoItemDTO> page = pedidoItemRepository
                .findByPedidoIdAndPedidoAlmacenSedeEmpresaIdAndEstadoIdNot(
                        requestedId,
                        empresa.getId(),
                        2,
                        pageable)
                .map(pedidoItemMapper::toDto);
        return page.hasContent()
                ? ResponseEntity.ok(page)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putPedidoItem(@PathVariable Long requestedId,
            @RequestBody PedidoItemDTO pedidoItemDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (pedidoItemRepository.existsByIdAndPedidoAlmacenSedeEmpresaId(
                requestedId, empresa.getId())
                && pedidoRepository.existsByIdAndAlmacenSedeEmpresaId(
                        pedidoItemDTOUpdate.getPedido(), empresa.getId())
                && productoPresentacionRepository.existsByIdAndProductoEmpresaId(
                        pedidoItemDTOUpdate.getProductoPresentacion(), empresa.getId())) {
            PedidoItemDTO updatePedidoItemDTO = new PedidoItemDTO(
                    requestedId,
                    pedidoItemDTOUpdate.getPedido(),
                    pedidoItemDTOUpdate.getProductoPresentacion(),
                    pedidoItemDTOUpdate.getCantidad(),
                    pedidoItemDTOUpdate.getEstado());
            PedidoItem updatedPedidoItem = pedidoItemMapper.toEntity(updatePedidoItemDTO);
            pedidoItemRepository.save(updatedPedidoItem);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deletePedidoItem(@PathVariable Long id) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            Empresa empresa = getEmpresaFromUser(authenticatedUser);
            if (pedidoItemRepository.existsByIdAndPedidoAlmacenSedeEmpresaId(id, empresa.getId())) {
                pedidoItemRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
