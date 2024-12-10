package com.coagronet.kardex.controllers;

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

import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/kardex")
@CrossOrigin(origins = "*")
public class KardexController {

    private final KardexRepository kardexRepository;
    private final KardexMapper kardexMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final AlmacenRepository almacenRepository;

    public KardexController(
            KardexRepository kardexRepository,
            KardexMapper kardexMapper,
            UserRoleRepository userRoleRepository,
            UserRepository userRepository,
            AlmacenRepository almacenRepository) {
        this.kardexRepository = kardexRepository;
        this.kardexMapper = kardexMapper;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.almacenRepository = almacenRepository;
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
    public ResponseEntity<KardexDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return kardexRepository
                .findByIdAndAlmacenSedeEmpresaId(requestedId, empresa.getId())
                .map(kardexMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createKardex(@RequestBody KardexDTO newKardexRequest, UriComponentsBuilder ucb) {
        KardexDTO newKardex = new KardexDTO(
                null,
                newKardexRequest.getFechaHora(),
                newKardexRequest.getAlmacen(),
                newKardexRequest.getProduccion(),
                newKardexRequest.getTipoMovimiento(),
                newKardexRequest.getDescripcion(),
                newKardexRequest.getEstado());
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (almacenRepository.existsByIdAndSedeEmpresaIdAndEstadoIdNot(
                newKardex.getAlmacen(),
                empresa.getId(),
                2)) {
            Kardex savedKardex = kardexMapper.toEntity(newKardex);
            kardexRepository.save(savedKardex);
            URI locationOfNewSede = ucb
                    .path("/api/v1/kardex/{id}")
                    .buildAndExpand(savedKardex.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewSede).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<Page<KardexDTO>> findAll(@PageableDefault Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Page<KardexDTO> page = kardexRepository
                .findByAlmacenSedeEmpresaIdAndEstadoIdNot(
                        empresa.getId(), 2, pageable)
                .map(kardexMapper::toDto);
        return page.hasContent()
                ? ResponseEntity.ok(page)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putKardex(
            @PathVariable Integer requestedId,
            @RequestBody KardexDTO kardexDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Kardex kardex = kardexRepository.findByIdAndAlmacenSedeEmpresaId(requestedId, empresa.getId())
                .orElse(null);
        if (null != kardex) {
            KardexDTO updateKardexDTO = new KardexDTO(
                    requestedId,
                    kardexDTOUpdate.getFechaHora(),
                    kardexDTOUpdate.getAlmacen(),
                    kardexDTOUpdate.getProduccion(),
                    kardexDTOUpdate.getTipoMovimiento(),
                    kardexDTOUpdate.getDescripcion(),
                    kardexDTOUpdate.getEstado());
            Kardex updatedKardex = kardexMapper.toEntity(updateKardexDTO);
            kardexRepository.save(updatedKardex);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteKardex(@PathVariable Integer id) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            Empresa empresa = getEmpresaFromUser(authenticatedUser);
            if (kardexRepository.existsByIdAndAlmacenSedeEmpresaId(id, empresa.getId())) {
                kardexRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
