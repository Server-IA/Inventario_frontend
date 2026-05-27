package com.coagronet.pasantia.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.pasantia.dto.InventarioAsignadoDTO;
import com.coagronet.pasantia.repository.PasantiaInventarioRepository;
import com.coagronet.utils.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasantiaInventarioService {

    private final PasantiaInventarioRepository inventarioRepository;
    private final AuthenticatedUser authenticatedUser;

    @Transactional(readOnly = true)
    public List<InventarioAsignadoDTO> getInventariosAsignados() {
        Long usuarioId = authenticatedUser.getCurrentUserId();

        if (usuarioId == null) {
            return List.of();
        }

        List<com.coagronet.pasantia.entity.Inventario> inventarios = inventarioRepository
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
}
