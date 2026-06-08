package com.coagronet.rol.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coagronet.estado.Estado;
import com.coagronet.rol.Rol;
import com.coagronet.rol.dtos.RolRequestDTO;
import com.coagronet.rol.dtos.RolResponseDTO;
import com.coagronet.rol.mappers.RolMapper;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.rol.services.impl.RolServiceImpl;
import com.coagronet.utils.AuthenticatedUser;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class RolServiceImplTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private RolMapper rolMapper;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @InjectMocks
    private RolServiceImpl rolService;

    @Test
    void create_throwsIllegalArgumentException_whenNombreDuplicado() {
        RolRequestDTO request = buildRequest("Operario");
        when(rolRepository.existsByNombreIgnoreCase("Operario")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> rolService.create(request));

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void create_setsAuditAndReturnsDto_whenRequestIsValid() {
        RolRequestDTO request = buildRequest("Operario");
        Rol mapped = Rol.builder().nombre("Operario").descripcion("Rol operativo").estado(buildEstado(1L)).build();
        Rol saved = Rol.builder()
                .id(10L)
                .nombre("Operario")
                .descripcion("Rol operativo")
                .estado(buildEstado(1L))
                .createdAt(Instant.now())
                .build();

        RolResponseDTO expected = new RolResponseDTO(10L, "Operario", "Rol operativo", 1L, "Activo", "admin",
                saved.getCreatedAt(), null, null);

        when(rolRepository.existsByNombreIgnoreCase("Operario")).thenReturn(false);
        when(rolMapper.toEntity(request)).thenReturn(mapped);
        when(authenticatedUser.getCurrentUserId()).thenReturn(77L);
        when(rolRepository.save(any(Rol.class))).thenReturn(saved);
        when(rolMapper.toDTO(saved)).thenReturn(expected);

        RolResponseDTO result = rolService.create(request);

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<Rol> captor = ArgumentCaptor.forClass(Rol.class);
        verify(rolRepository).save(captor.capture());
        Rol persisted = captor.getValue();

        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getCreatedBy()).isNotNull();
        assertThat(persisted.getCreatedBy().getId()).isEqualTo(77L);
    }

    @Test
    void update_throwsEntityNotFoundException_whenRolDoesNotExist() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rolService.update(99L, buildRequest("Operario")));

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void update_throwsIllegalStateException_whenRolWasSoftDeleted() {
        Rol existing = Rol.builder().id(11L).nombre("Operario").deletedAt(Instant.now()).build();
        when(rolRepository.findById(11L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> rolService.update(11L, buildRequest("Operario")));

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void softDelete_setsDeletedAtAndDeletedBy_whenRolIsActive() {
        Rol existing = Rol.builder().id(12L).nombre("Operario").build();
        when(rolRepository.findById(12L)).thenReturn(Optional.of(existing));
        when(authenticatedUser.getCurrentUserId()).thenReturn(15L);

        rolService.softDelete(12L);

        ArgumentCaptor<Rol> captor = ArgumentCaptor.forClass(Rol.class);
        verify(rolRepository).save(captor.capture());
        Rol deleted = captor.getValue();

        assertThat(deleted.getDeletedAt()).isNotNull();
        assertThat(deleted.getDeletedBy()).isNotNull();
        assertThat(deleted.getDeletedBy().getId()).isEqualTo(15L);
    }

    @Test
    void getAll_excludesSoftDeletedRoles() {
        Rol active = Rol.builder().id(1L).nombre("Operario").estado(buildEstado(1L)).deletedAt(null).build();
        Rol deleted = Rol.builder().id(2L).nombre("Temporal").estado(buildEstado(1L)).deletedAt(Instant.now())
                .build();

        when(rolRepository.findAll()).thenReturn(List.of(active, deleted));
        when(rolMapper.toDTO(active)).thenReturn(
                new RolResponseDTO(1L, "Operario", "Rol operativo", 1L, "Activo", null, null, null, null));

        List<RolResponseDTO> result = rolService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().nombre()).isEqualTo("Operario");
    }

    private RolRequestDTO buildRequest(String nombre) {
        return new RolRequestDTO(nombre, "Rol operativo", 1L);
    }

    private Estado buildEstado(Long id) {
        Estado estado = new Estado();
        estado.setId(id);
        return estado;
    }
}
