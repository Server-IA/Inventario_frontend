/*=============================================================================
 Nombre del archivo : RolServiceImpl.java
 Descripcion        : Implementación del servicio para la gestión de roles.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-22 | 0.4.0   | JUAN JOSE CASTRO     | Reemplazo de OffsetDateTime |
 |            |         |                      | por Instant en la auditoría.|
 |            |         |                      | Eliminación de validaciones |
 |            |         |                      | y filtros manuales sobre el |
 |            |         |                      | campo deletedAt en los      |
 |            |         |                      | métodos de consulta,        |
 |            |         |                      | actualización y borrado.    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.rol.services.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.estado.Estado;
import com.coagronet.rol.Rol;
import com.coagronet.rol.dtos.RolRequestDTO;
import com.coagronet.rol.dtos.RolResponseDTO;
import com.coagronet.rol.mappers.RolMapper;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.rol.services.RolService;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticatedUser;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;
    private final AuthenticatedUser authenticatedUser;

    // ====================== CREATE =======================

    @Override
    public RolResponseDTO create(RolRequestDTO request) {

        // Validar nombre único
        if (rolRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + request.nombre());
        }

        Rol rol = rolMapper.toEntity(request);

        // Auditoría
        rol.setCreatedAt(Instant.now());

        Long userId = authenticatedUser.getCurrentUserId();
        if (userId != null) {
            User creator = new User();
            creator.setId(userId);
            rol.setCreatedBy(creator);
        }

        Rol saved = rolRepository.save(rol);

        return rolMapper.toDTO(saved);
    }

    // ====================== UPDATE =======================

    @Override
    public RolResponseDTO update(Long id, RolRequestDTO request) {

        Rol existing = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + id));

        // Validar nombre único si cambió
        if (!existing.getNombre().equalsIgnoreCase(request.nombre())
                && rolRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + request.nombre());
        }

        // Actualizar campos
        existing.setNombre(request.nombre());
        existing.setDescripcion(request.descripcion());

        // Estado (solo asignamos el id sin volver a BD)
        if (request.estadoId() != null) {
            Estado estado = existing.getEstado();
            if (estado == null) {
                estado = new Estado();
            }
            estado.setId(request.estadoId());
            existing.setEstado(estado);
        }

        // Auditoría
        existing.setUpdatedAt(Instant.now());

        Long userId = authenticatedUser.getCurrentUserId();
        if (userId != null) {
            User upd = new User();
            upd.setId(userId);
            existing.setUpdatedBy(upd);
        }

        Rol updated = rolRepository.save(existing);

        return rolMapper.toDTO(updated);
    }

    // ====================== GET BY ID =======================

    @Override
    @Transactional(readOnly = true)
    public RolResponseDTO getById(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + id));

        return rolMapper.toDTO(rol);
    }

    // ====================== LISTAR =======================

    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDTO> getAll() {
        return rolRepository.findAll()
                .stream()
                .map(rolMapper::toDTO)
                .toList();
    }

    // ====================== DELETE (borrado lógico) =======================

    @Override
    public void softDelete(Long id) {
        Rol existing = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + id));

        existing.setDeletedAt(Instant.now());

        Long userId = authenticatedUser.getCurrentUserId();
        if (userId != null) {
            User del = new User();
            del.setId(userId);
            existing.setDeletedBy(del);
        }

        rolRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Rol existing = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + id));

        rolRepository.delete(existing);
    }

}