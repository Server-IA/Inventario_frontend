package com.coagronet.kardexItem.controllers;

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
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.kardexItem.KardexItem;
import com.coagronet.kardexItem.dtos.KardexItemDTO;
import com.coagronet.kardexItem.mappers.KardexItemMapper;
import com.coagronet.kardexItem.repositories.KardexItemRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/kardexItem")
@CrossOrigin(origins = "*")
public class KardexItemController {

    private final KardexItemRepository kardexItemRepository;
    private final KardexItemMapper kardexItemMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final KardexRepository kardexRepository;

    private KardexItemController(
            KardexItemRepository kardexItemRepository,
            KardexItemMapper kardexItemMapper,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository,
            KardexRepository kardexRepository) {
        this.kardexItemRepository = kardexItemRepository;
        this.kardexItemMapper = kardexItemMapper;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.kardexRepository = kardexRepository;
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
    private ResponseEntity<KardexItemDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return kardexItemRepository
                .findByIdAndKardexAlmacenSedeEmpresaId(requestedId, empresa.getId())
                .map(kardexItemMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createKardexItem(@RequestBody KardexItemDTO newKardexItemRequest,
            UriComponentsBuilder ucb) {
        KardexItemDTO newKardexItem = new KardexItemDTO(
                null,
                newKardexItemRequest.getKardex(),
                newKardexItemRequest.getProductoPresentacion(),
                newKardexItemRequest.getCantidad(),
                newKardexItemRequest.getPrecio(),
                newKardexItemRequest.getEstado());
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (kardexRepository.existsByIdAndAlmacenSedeEmpresaId(
                newKardexItemRequest.getKardex(),
                empresa.getId())) {
            KardexItem savedKardexItem = kardexItemMapper.toEntity(newKardexItem);
            kardexItemRepository.save(savedKardexItem);
            URI locationOfNewKardexItem = ucb
                    .path("/api/v1/kardexItem/{id}")
                    .buildAndExpand(savedKardexItem.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewKardexItem).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    private ResponseEntity<Page<KardexItemDTO>> findAll(@PageableDefault Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<KardexItemDTO> page = kardexItemRepository
                .findByKardexAlmacenSedeEmpresaIdAndEstadoIdNot(
                        empresa.getId(), 2, pageable)
                .map(kardexItemMapper::toDto);
        return page.hasContent()
                ? ResponseEntity.ok(page)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putKardexItem(
            @PathVariable Integer requestedId,
            @RequestBody KardexItemDTO kardexItemDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        KardexItem kardexItem = kardexItemRepository.findByIdAndKardexAlmacenSedeEmpresaId(
                requestedId, empresa.getId()).orElse(null);
        if (null != kardexItem) {
            KardexItemDTO updateKardexItemDTO = new KardexItemDTO(
                    requestedId,
                    kardexItemDTOUpdate.getKardex(),
                    kardexItemDTOUpdate.getProductoPresentacion(),
                    kardexItemDTOUpdate.getCantidad(),
                    kardexItemDTOUpdate.getPrecio(),
                    kardexItemDTOUpdate.getEstado());
            KardexItem updatedKardexItem = kardexItemMapper.toEntity(updateKardexItemDTO);
            kardexItemRepository.save(updatedKardexItem);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteKardexItem(@PathVariable Integer id) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            Empresa empresa = getEmpresaFromUser(authenticatedUser);
            if (kardexItemRepository.existsByIdAndKardexAlmacenSedeEmpresaId(id, empresa.getId())) {
                kardexItemRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
