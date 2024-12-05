package com.coagronet.tipoMovimiento.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoDTO;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoMinimalDTO;
import com.coagronet.tipoMovimiento.mappers.TipoMovimientoMapper;
import com.coagronet.tipoMovimiento.reposritories.TipoMovimientoRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/tipo_movimiento")
@CrossOrigin(origins = "*")
public class TipoMovimientoController {

        private final TipoMovimientoRepository tipoMovimientoRepository;
        private final TipoMovimientoMapper tipoMovimientoMapper;
        private final EstadoRepository estadoRepository;
        private final UserRoleRepository userRoleRepository;
        private final UserRepository userRepository;

        private TipoMovimientoController(
                        TipoMovimientoRepository tipoMovimientoRepository,
                        TipoMovimientoMapper tipoMovimientoMapper,
                        EstadoRepository estadoRepository,
                        UserRoleRepository userRoleRepository,
                        UserRepository userRepository) {
                this.tipoMovimientoRepository = tipoMovimientoRepository;
                this.tipoMovimientoMapper = tipoMovimientoMapper;
                this.estadoRepository = estadoRepository;
                this.userRoleRepository = userRoleRepository;
                this.userRepository = userRepository;
        }

        private Empresa getEmpresaFromUser(User user) {
                return userRoleRepository.findByUser(user).stream()
                                .map(UserRole::getEmpresa)
                                .findFirst()
                                .orElseThrow(
                                                () -> new RuntimeException("Empresa no encontrada para el usuario"));
        }

        private User getAuthenticatedUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(
                                                () -> new UsernameNotFoundException("Usuario no encontrado"));
        }

        @GetMapping("/{requestedId}")
        private ResponseEntity<TipoMovimientoDTO> findById(@PathVariable Integer requestedId) {
                User authenticatedUser = getAuthenticatedUser();
                Empresa empresa = getEmpresaFromUser(authenticatedUser);
                return tipoMovimientoRepository.findByIdAndEmpresaIdAndEstadoIdNot(
                                requestedId,
                                empresa.getId(),
                                2)
                                .map(tipoMovimientoMapper::toDto)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        private ResponseEntity<Void> createTipoMovimiento(@RequestBody TipoMovimientoDTO newTipoMovimientoRequest,
                        UriComponentsBuilder ucb) {
                User authenticatedUser = getAuthenticatedUser();
                Empresa empresa = getEmpresaFromUser(authenticatedUser);
                TipoMovimientoDTO newTipoMovimiento = new TipoMovimientoDTO(
                                null,
                                newTipoMovimientoRequest.getNombre(),
                                newTipoMovimientoRequest.getDescripcion(),
                                newTipoMovimientoRequest.getEstado(),
                                empresa.getId());
                TipoMovimiento savedTipoMovimiento = tipoMovimientoMapper.toEntity(newTipoMovimiento);
                tipoMovimientoRepository.save(savedTipoMovimiento);
                URI locationOfNewTipoMovimiento = ucb
                                .path("/api/v1/tipo_movimiento/{id}")
                                .buildAndExpand(savedTipoMovimiento.getId())
                                .toUri();
                return ResponseEntity.created(locationOfNewTipoMovimiento).build();
        }

        @GetMapping
        private ResponseEntity<List<TipoMovimientoDTO>> findAll() {
                User authenticatedUser = getAuthenticatedUser();
                Empresa empresa = getEmpresaFromUser(authenticatedUser);

                List<TipoMovimientoDTO> tipoMovimientoDTOs = tipoMovimientoRepository
                                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                                .stream()
                                .map(tipoMovimientoMapper::toDto)
                                .collect(Collectors.toList());

                return tipoMovimientoDTOs.isEmpty()
                                ? ResponseEntity.noContent().build()
                                : ResponseEntity.ok(tipoMovimientoDTOs);
        }

        @GetMapping("/minimal")
        private ResponseEntity<List<TipoMovimientoMinimalDTO>> findAllMinimal() {
                User authenticatedUser = getAuthenticatedUser();
                Empresa empresa = getEmpresaFromUser(authenticatedUser);

                List<TipoMovimientoMinimalDTO> tipoMovimientoDTOs = tipoMovimientoRepository
                                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2)
                                .stream()
                                .map(tipoMovimientoMapper::toMinimalDto)
                                .collect(Collectors.toList());

                return tipoMovimientoDTOs.isEmpty()
                                ? ResponseEntity.noContent().build()
                                : ResponseEntity.ok(tipoMovimientoDTOs);
        }

        @PutMapping("/{requestedId}")
        private ResponseEntity<Void> putTipoMovimiento(@PathVariable Integer requestedId,
                        @RequestBody TipoMovimientoDTO tipoMovimientoDTOUpdate) {
                User authenticatedUser = getAuthenticatedUser();
                Empresa empresa = getEmpresaFromUser(authenticatedUser);
                TipoMovimiento tipoMovimiento = tipoMovimientoRepository
                                .findByIdAndEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                                .orElse(null);
                if (null != tipoMovimiento) {
                        TipoMovimientoDTO updateTipoMovimientoDTO = new TipoMovimientoDTO(
                                        requestedId,
                                        tipoMovimientoDTOUpdate.getNombre(),
                                        tipoMovimientoDTOUpdate.getDescripcion(),
                                        tipoMovimientoDTOUpdate.getEstado(),
                                        empresa.getId());
                        TipoMovimiento updatedTipoMovimiento = tipoMovimientoMapper.toEntity(updateTipoMovimientoDTO);
                        tipoMovimientoRepository.save(updatedTipoMovimiento);
                        return ResponseEntity.noContent().build();
                }
                return ResponseEntity.notFound().build();
        }

        @DeleteMapping("/{id}")
        private ResponseEntity<Void> deleteTipoMovimiento(@PathVariable Integer id) {
                User authenticatedUser = getAuthenticatedUser();
                Empresa empresa = getEmpresaFromUser(authenticatedUser);
                if (tipoMovimientoRepository.existsByIdAndEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
                        TipoMovimiento tipoMovimiento = tipoMovimientoRepository.findById(id).orElse(null);
                        Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
                        tipoMovimiento.setEstado(estadoInactivo);
                        tipoMovimientoRepository.save(tipoMovimiento);
                        return ResponseEntity.noContent().build();
                }
                return ResponseEntity.notFound().build();
        }

}