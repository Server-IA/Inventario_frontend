package com.coagronet.modulo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.custom.EntidadNoEncontradaException;
import com.coagronet.exceptionHandler.custom.RecursoDuplicadoException;
import com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException;
import com.coagronet.modulo.Modulo;
import com.coagronet.modulo.dtos.ModuloDetailResponse;
import com.coagronet.modulo.dtos.ModuloRequest;
import com.coagronet.modulo.repositories.ModuloRepository;
import com.coagronet.subsistema.SubSistema;
import com.coagronet.subsistema.repositories.SubSistemaRepository;
import com.coagronet.tipoaplicacion.TipoAplicacion;
import com.coagronet.tipoaplicacion.repositories.TipoAplicacionRepository;
import com.coagronet.tipomodulo.TipoModulo;
import com.coagronet.tipomodulo.repositories.TipoModuloRepository;

@ExtendWith(MockitoExtension.class)
class ModuloServiceTest {

    @Mock
    private ModuloRepository moduloRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private SubSistemaRepository subSistemaRepository;

    @Mock
    private TipoModuloRepository tipoModuloRepository;

    @Mock
    private TipoAplicacionRepository tipoAplicacionRepository;

    @InjectMocks
    private ModuloService moduloService;

    @Test
    void crearModulo_throwsRecursoDuplicadoException_whenNombreExists() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        when(moduloRepository.existsByNombre(request.nombre())).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> moduloService.crearModulo(request));

        verify(moduloRepository, never()).save(any(Modulo.class));
        verifyNoInteractions(estadoRepository, subSistemaRepository, tipoModuloRepository, tipoAplicacionRepository);
    }

    @Test
    void crearModulo_throwsEntidadNoEncontradaException_whenEstadoMissing() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        when(moduloRepository.existsByNombre(request.nombre())).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> moduloService.crearModulo(request));

        verify(subSistemaRepository, never()).findById(any());
        verify(tipoModuloRepository, never()).findById(any());
        verify(tipoAplicacionRepository, never()).findById(any());
        verify(moduloRepository, never()).save(any(Modulo.class));
    }

    @Test
    void crearModulo_throwsEntidadNoEncontradaException_whenSubSistemaMissing() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        when(moduloRepository.existsByNombre(request.nombre())).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.of(buildEstado(request.estadoId())));
        when(subSistemaRepository.findById(request.subSistemaId())).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> moduloService.crearModulo(request));

        verify(tipoModuloRepository, never()).findById(any());
        verify(tipoAplicacionRepository, never()).findById(any());
        verify(moduloRepository, never()).save(any(Modulo.class));
    }

    @Test
    void crearModulo_throwsEntidadNoEncontradaException_whenTipoModuloMissing() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        when(moduloRepository.existsByNombre(request.nombre())).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.of(buildEstado(request.estadoId())));
        when(subSistemaRepository.findById(request.subSistemaId())).thenReturn(Optional.of(buildSubSistema(request.subSistemaId())));
        when(tipoModuloRepository.findById(request.tipoModuloId())).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> moduloService.crearModulo(request));

        verify(tipoAplicacionRepository, never()).findById(any());
        verify(moduloRepository, never()).save(any(Modulo.class));
    }

    @Test
    void crearModulo_throwsEntidadNoEncontradaException_whenTipoAplicacionMissing() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        when(moduloRepository.existsByNombre(request.nombre())).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.of(buildEstado(request.estadoId())));
        when(subSistemaRepository.findById(request.subSistemaId())).thenReturn(Optional.of(buildSubSistema(request.subSistemaId())));
        when(tipoModuloRepository.findById(request.tipoModuloId())).thenReturn(Optional.of(buildTipoModulo(request.tipoModuloId())));
        when(tipoAplicacionRepository.findById(request.tipoAplicacionId())).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> moduloService.crearModulo(request));

        verify(moduloRepository, never()).save(any(Modulo.class));
    }

    @Test
    void crearModulo_persistsModulo_whenRequestIsValid() {
        ModuloRequest request = buildRequest(List.of("ADMIN", "USER"));
        when(moduloRepository.existsByNombre(request.nombre())).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.of(buildEstado(request.estadoId())));
        when(subSistemaRepository.findById(request.subSistemaId())).thenReturn(Optional.of(buildSubSistema(request.subSistemaId())));
        when(tipoModuloRepository.findById(request.tipoModuloId())).thenReturn(Optional.of(buildTipoModulo(request.tipoModuloId())));
        when(tipoAplicacionRepository.findById(request.tipoAplicacionId()))
                .thenReturn(Optional.of(buildTipoAplicacion(request.tipoAplicacionId())));

        Modulo saved = new Modulo();
        saved.setId(99L);
        when(moduloRepository.save(any(Modulo.class))).thenReturn(saved);

        Long result = moduloService.crearModulo(request);

        assertThat(result).isEqualTo(99L);

        ArgumentCaptor<Modulo> captor = ArgumentCaptor.forClass(Modulo.class);
        verify(moduloRepository).save(captor.capture());
        Modulo persisted = captor.getValue();

        assertThat(persisted.getNombre()).isEqualTo(request.nombre());
        assertThat(persisted.getUrl()).isEqualTo(request.url());
        assertThat(persisted.getDescripcion()).isEqualTo(request.descripcion());
        assertThat(persisted.getIcon()).isEqualTo(request.icon());
        assertThat(persisted.getNombreId()).isEqualTo(request.nombreId());
        assertThat(persisted.getRequerido()).isEqualTo(request.requerido());
        assertThat(persisted.getEstado().getId()).isEqualTo(request.estadoId());
        assertThat(persisted.getSubSistema().getId()).isEqualTo(request.subSistemaId());
        assertThat(persisted.getTipoModulo().getId()).isEqualTo(request.tipoModuloId());
        assertThat(persisted.getTipoAplicacion().getId()).isEqualTo(request.tipoAplicacionId());
        assertArrayEquals(new String[] { "ADMIN", "USER" }, persisted.getRolId());
    }

    @Test
    void crearModulo_setsRolIdToNull_whenRolesIsEmpty() {
        ModuloRequest request = buildRequest(Collections.emptyList());
        when(moduloRepository.existsByNombre(request.nombre())).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.of(buildEstado(request.estadoId())));
        when(subSistemaRepository.findById(request.subSistemaId())).thenReturn(Optional.of(buildSubSistema(request.subSistemaId())));
        when(tipoModuloRepository.findById(request.tipoModuloId())).thenReturn(Optional.of(buildTipoModulo(request.tipoModuloId())));
        when(tipoAplicacionRepository.findById(request.tipoAplicacionId()))
                .thenReturn(Optional.of(buildTipoAplicacion(request.tipoAplicacionId())));

        Modulo saved = new Modulo();
        saved.setId(100L);
        when(moduloRepository.save(any(Modulo.class))).thenReturn(saved);

        moduloService.crearModulo(request);

        ArgumentCaptor<Modulo> captor = ArgumentCaptor.forClass(Modulo.class);
        verify(moduloRepository).save(captor.capture());
        Modulo persisted = captor.getValue();

        assertNull(persisted.getRolId());
    }

    @Test
    void actualizarModulo_throwsRecursoNoEncontradoException_whenModuloMissing() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        when(moduloRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> moduloService.actualizarModulo(10L, request));

        verify(moduloRepository, never()).save(any(Modulo.class));
        verify(moduloRepository, never()).existsByNombreAndIdNot(any(), any());
        verifyNoInteractions(estadoRepository, subSistemaRepository, tipoModuloRepository, tipoAplicacionRepository);
    }

    @Test
    void actualizarModulo_throwsRecursoDuplicadoException_whenNombreExistsOnOtherModulo() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        Modulo existing = new Modulo();
        existing.setId(10L);
        when(moduloRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(moduloRepository.existsByNombreAndIdNot(request.nombre(), 10L)).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> moduloService.actualizarModulo(10L, request));

        verify(moduloRepository, never()).save(any(Modulo.class));
        verifyNoInteractions(estadoRepository, subSistemaRepository, tipoModuloRepository, tipoAplicacionRepository);
    }

    @Test
    void actualizarModulo_throwsEntidadNoEncontradaException_whenEstadoMissing() {
        ModuloRequest request = buildRequest(List.of("ADMIN"));
        Modulo existing = new Modulo();
        existing.setId(10L);
        when(moduloRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(moduloRepository.existsByNombreAndIdNot(request.nombre(), 10L)).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> moduloService.actualizarModulo(10L, request));

        verify(moduloRepository, never()).save(any(Modulo.class));
    }

    @Test
    void actualizarModulo_persistsUpdatedFields_whenRequestIsValid() {
        Long moduloId = 10L;
        ModuloRequest request = buildRequest(List.of("ADMIN", "USER"));

        Modulo existing = new Modulo();
        existing.setId(moduloId);

        when(moduloRepository.findById(moduloId)).thenReturn(Optional.of(existing));
        when(moduloRepository.existsByNombreAndIdNot(request.nombre(), moduloId)).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.of(buildEstado(request.estadoId())));
        when(subSistemaRepository.findById(request.subSistemaId()))
                .thenReturn(Optional.of(buildSubSistema(request.subSistemaId())));
        when(tipoModuloRepository.findById(request.tipoModuloId()))
                .thenReturn(Optional.of(buildTipoModulo(request.tipoModuloId())));
        when(tipoAplicacionRepository.findById(request.tipoAplicacionId()))
                .thenReturn(Optional.of(buildTipoAplicacion(request.tipoAplicacionId())));

        when(moduloRepository.save(any(Modulo.class))).thenAnswer(inv -> inv.getArgument(0));

        moduloService.actualizarModulo(moduloId, request);

        ArgumentCaptor<Modulo> captor = ArgumentCaptor.forClass(Modulo.class);
        verify(moduloRepository).save(captor.capture());
        Modulo updated = captor.getValue();

        assertThat(updated.getId()).isEqualTo(moduloId);
        assertThat(updated.getNombre()).isEqualTo(request.nombre());
        assertThat(updated.getUrl()).isEqualTo(request.url());
        assertThat(updated.getDescripcion()).isEqualTo(request.descripcion());
        assertThat(updated.getIcon()).isEqualTo(request.icon());
        assertThat(updated.getNombreId()).isEqualTo(request.nombreId());
        assertThat(updated.getRequerido()).isEqualTo(request.requerido());
        assertThat(updated.getEstado().getId()).isEqualTo(request.estadoId());
        assertThat(updated.getSubSistema().getId()).isEqualTo(request.subSistemaId());
        assertThat(updated.getTipoModulo().getId()).isEqualTo(request.tipoModuloId());
        assertThat(updated.getTipoAplicacion().getId()).isEqualTo(request.tipoAplicacionId());
        assertArrayEquals(new String[] { "ADMIN", "USER" }, updated.getRolId());
    }

    @Test
    void actualizarModulo_setsRolIdToNull_whenRolesIsEmpty() {
        Long moduloId = 10L;
        ModuloRequest request = buildRequest(Collections.emptyList());

        Modulo existing = new Modulo();
        existing.setId(moduloId);

        when(moduloRepository.findById(moduloId)).thenReturn(Optional.of(existing));
        when(moduloRepository.existsByNombreAndIdNot(request.nombre(), moduloId)).thenReturn(false);
        when(estadoRepository.findById(request.estadoId())).thenReturn(Optional.of(buildEstado(request.estadoId())));
        when(subSistemaRepository.findById(request.subSistemaId()))
                .thenReturn(Optional.of(buildSubSistema(request.subSistemaId())));
        when(tipoModuloRepository.findById(request.tipoModuloId()))
                .thenReturn(Optional.of(buildTipoModulo(request.tipoModuloId())));
        when(tipoAplicacionRepository.findById(request.tipoAplicacionId()))
                .thenReturn(Optional.of(buildTipoAplicacion(request.tipoAplicacionId())));

        when(moduloRepository.save(any(Modulo.class))).thenAnswer(inv -> inv.getArgument(0));

        moduloService.actualizarModulo(moduloId, request);

        ArgumentCaptor<Modulo> captor = ArgumentCaptor.forClass(Modulo.class);
        verify(moduloRepository).save(captor.capture());
        Modulo updated = captor.getValue();
        assertNull(updated.getRolId());
    }

    @Test
    void obtenerDetalleModulo_returnsProjection_whenExists() {
        when(moduloRepository.findByIdProjected(10L)).thenReturn(Optional.of(new ModuloDetailResponse(
                "Inventario",
                "/inventario",
                "Modulo de inventario",
                "fa-box",
                1L,
                2L,
                3L,
                4L,
                new String[] { "ADMIN" },
                "mod_inventario",
                true)));

        ModuloDetailResponse result = moduloService.obtenerDetalleModulo(10L);
        assertThat(result.nombre()).isEqualTo("Inventario");
    }

    @Test
    void obtenerDetalleModulo_throwsRecursoNoEncontradoException_whenMissing() {
        when(moduloRepository.findByIdProjected(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> moduloService.obtenerDetalleModulo(10L));
    }

    private ModuloRequest buildRequest(List<String> roles) {
        return new ModuloRequest(
                "Inventario",
                "/inventario",
                "Modulo de inventario",
                "fa-box",
                1L,
                2L,
                3L,
                4L,
                roles,
                "mod_inventario",
                true);
    }

    private Estado buildEstado(Long id) {
        Estado estado = new Estado();
        estado.setId(id);
        return estado;
    }

    private SubSistema buildSubSistema(Long id) {
        SubSistema subSistema = new SubSistema();
        subSistema.setId(id);
        return subSistema;
    }

    private TipoModulo buildTipoModulo(Long id) {
        TipoModulo tipoModulo = new TipoModulo();
        tipoModulo.setId(id);
        return tipoModulo;
    }

    private TipoAplicacion buildTipoAplicacion(Long id) {
        TipoAplicacion tipoAplicacion = new TipoAplicacion();
        tipoAplicacion.setId(id);
        return tipoAplicacion;
    }
}

