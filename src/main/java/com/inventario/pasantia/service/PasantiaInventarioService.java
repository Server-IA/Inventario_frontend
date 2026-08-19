package com.inventario.pasantia.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.exceptionHandler.custom.RecursoNoEncontradoException;
import com.inventario.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.inventario.pasantia.dto.InventarioAsignadoDTO;
import com.inventario.pasantia.dto.InventarioProgresoItemDTO;
import com.inventario.pasantia.dto.InventarioProgresoRequestDTO;
import com.inventario.pasantia.dto.InventarioProgresoResponseDTO;
import com.inventario.pasantia.dto.MensajeResponseDTO;
import com.inventario.pasantia.entity.CondicionItem;
import com.inventario.pasantia.entity.InventarioProgreso;
import com.inventario.pasantia.entity.InventarioProgresoId;
import com.inventario.pasantia.repository.PasantiaInventarioProgresoRepository;
import com.inventario.pasantia.repository.PasantiaInventarioRepository;
import com.inventario.utils.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasantiaInventarioService {

        private final PasantiaInventarioRepository inventarioRepository;
        private final PasantiaInventarioProgresoRepository inventarioProgresoRepository;
        private final AuthenticatedUser authenticatedUser;
        private final EmpresaTenantIdentifierResolver empresaTenantIdentifierResolver;
        private final com.inventario.usuariorol.repositories.UsuarioRolRepository usuarioRolRepository;

        @Transactional
        public Long crearInventario(com.inventario.pasantia.dto.InventarioCreateRequestDTO request) {
                Long empId = empresaTenantIdentifierResolver.resolveCurrentTenantIdentifier();
                if (empId == null) {
                        throw new IllegalStateException("No se pudo determinar la empresa del usuario actual");
                }

                boolean perteneceEmpresa = usuarioRolRepository.existsByUser_IdAndEmpresa_IdAndDeletedAtIsNull(
                                request.getUsuarioAsignadoId(), empId);

                if (!perteneceEmpresa) {
                        throw new IllegalArgumentException("El usuario asignado no pertenece a su empresa");
                }

                com.inventario.pasantia.entity.Inventario nuevoInventario = com.inventario.pasantia.entity.Inventario
                                .builder()
                                .empId(empId)
                                .nombre(request.getNombre())
                                .descripcion(request.getDescripcion())
                                .fechaHora(request.getFechaHora())
                                .subseccion(com.inventario.pasantia.entity.Subseccion.builder()
                                                .id(request.getSubseccionId()).build())
                                .estado(com.inventario.pasantia.entity.EstadoInventario.builder().id((short) 1).build())
                                .usuarioAsignadoId(request.getUsuarioAsignadoId())
                                .build();

                nuevoInventario = inventarioRepository.save(nuevoInventario);
                return nuevoInventario.getId();
        }

        @Transactional(readOnly = true)
        public List<InventarioAsignadoDTO> getInventariosAsignados() {
                Long usuarioId = authenticatedUser.getCurrentUserId();

                if (usuarioId == null) {
                        return List.of();
                }

                List<com.inventario.pasantia.entity.Inventario> inventarios = inventarioRepository
                                .findByUsuarioAsignadoIdWithDetails(usuarioId);

                return inventarios.stream().map(inv -> InventarioAsignadoDTO.builder()
                                .id(inv.getId())
                                .nombre(inv.getNombre())
                                .descripcion(inv.getDescripcion())
                                .fechaHora(inv.getFechaHora())
                                .subSeccionId(inv.getSubseccion() != null ? inv.getSubseccion().getId() : null)
                                .subSeccionNombre(inv.getSubseccion() != null ? inv.getSubseccion().getNombre() : null)
                                .seccionNombre((inv.getSubseccion() != null && inv.getSubseccion().getSeccion() != null)
                                                ? inv.getSubseccion().getSeccion().getNombre()
                                                : null)
                                .estadoId(inv.getEstado() != null ? inv.getEstado().getId() : null)
                                .estadoNombre(inv.getEstado() != null ? inv.getEstado().getNombre() : null)
                                .usuarioAsignadoId(inv.getUsuarioAsignadoId())
                                .build()).collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public InventarioProgresoResponseDTO getProgresoByInventarioId(Long inventarioId) {
                List<InventarioProgreso> progresos = inventarioProgresoRepository.findByIdInventarioId(inventarioId);

                List<InventarioProgresoItemDTO> items = progresos.stream()
                                .map(p -> InventarioProgresoItemDTO.builder()
                                                .productoIdentificador(p.getId().getProductoIdentificador())
                                                .encontrado(p.getEncontrado())
                                                .estado(p.getEstado() != null ? p.getEstado().name() : null)
                                                .observacion(p.getObservacion())
                                                .build())
                                .collect(Collectors.toList());

                return InventarioProgresoResponseDTO.builder()
                                .inventarioId(inventarioId)
                                .items(items)
                                .build();
        }

        @Transactional
        public MensajeResponseDTO guardarProgreso(Long inventarioId, InventarioProgresoRequestDTO request) {
                com.inventario.pasantia.entity.Inventario inventario = inventarioRepository.findById(inventarioId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado",
                                                inventarioId));

                List<InventarioProgreso> progresos = request.getItems().stream().map(item -> {
                        InventarioProgresoId id = new InventarioProgresoId(inventarioId,
                                        item.getProductoIdentificador());
                        return InventarioProgreso.builder()
                                        .id(id)
                                        .inventario(inventario)
                                        .empId(inventario.getEmpId())
                                        .encontrado(item.getEncontrado())
                                        .estado(CondicionItem.valueOf(item.getEstado()))
                                        .observacion(item.getObservacion())
                                        .build();
                }).collect(Collectors.toList());

                inventarioProgresoRepository.saveAll(progresos);

                return MensajeResponseDTO.builder()
                                .mensaje("Progreso guardado correctamente")
                                .build();
        }

        @Transactional
        public MensajeResponseDTO finalizarInventario(Long inventarioId, InventarioProgresoRequestDTO request) {
                guardarProgreso(inventarioId, request);

                com.inventario.pasantia.entity.Inventario inventario = inventarioRepository.findById(inventarioId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado",
                                                inventarioId));

                inventario.setEstado(com.inventario.pasantia.entity.EstadoInventario.builder().id((short) 3).build());
                inventarioRepository.save(inventario);

                return MensajeResponseDTO.builder()
                                .mensaje("Inventario finalizado correctamente")
                                .build();
        }
}
