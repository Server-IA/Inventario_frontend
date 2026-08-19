/*=============================================================================
 Nombre del archivo : EmpresaServiceDetalleTest.java
 Descripcion        : Pruebas unitarias del detalle y alcance de empresa.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion de pruebas del detalle completo y control multitenant de la HU-043.3.                                                     |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.empresa.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;

import com.inventario.empresa.Empresa;
import com.inventario.empresa.dtos.EmpresaDetalleResponseDTO;
import com.inventario.empresa.repositories.EmpresaRepository;
import com.inventario.estado.Estado;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.custom.RecursoNoEncontradoException;
import com.inventario.persona.Persona;
import com.inventario.persona.repositories.PersonaRepository;
import com.inventario.tipoIdentificacion.TipoIdentificacion;
import com.inventario.tipoIdentificacion.repositories.TipoIdentificacionRepository;
import com.inventario.utils.UserEmpresaService;
import com.inventario.utils.UserRoleService;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceDetalleTest {

	@Mock
	private EmpresaRepository empresaRepository;

	@Mock
	private UserEmpresaService userEmpresaService;

	@Mock
	private UserRoleService userRoleService;

	@Mock
	private TipoIdentificacionRepository tipoIdentificacionRepository;

	@Mock
	private PersonaRepository personaRepository;

	@Mock
	private EstadoRepository estadoRepository;

	@Mock
	private MessageSource messageSource;

	private EmpresaService empresaService;

	@BeforeEach
	void setUp() {
		empresaService = new EmpresaService(empresaRepository, userEmpresaService, userRoleService,
				tipoIdentificacionRepository, personaRepository, estadoRepository, messageSource);
	}

	@Test
	void obtenerDetalle_administradorSistemaPuedeConsultarCualquierEmpresa() {
		when(userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA")).thenReturn(true);
		when(empresaRepository.buscarDetallePorId(45L)).thenReturn(Optional.of(empresaCompleta(45L)));

		EmpresaDetalleResponseDTO detalle = empresaService.obtenerDetalle(45L);

		assertEquals(45L, detalle.getId());
		assertEquals("Empresa Demo", detalle.getNombre());
		verify(userEmpresaService, never()).getEmpresaIdFromCurrentRequest();
	}

	@Test
	void obtenerDetalle_usuarioEmpresaPuedeConsultarSuPropiaEmpresa() {
		when(userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA")).thenReturn(false);
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(77L);
		when(empresaRepository.buscarDetallePorId(77L)).thenReturn(Optional.of(empresaCompleta(77L)));

		EmpresaDetalleResponseDTO detalle = empresaService.obtenerDetalle(77L);

		assertEquals(77L, detalle.getId());
		assertEquals(5L, detalle.getTipoIdentificacionId());
		assertEquals("NIT", detalle.getTipoIdentificacionNombre());
		assertEquals("900123456", detalle.getIdentificacion());
		assertEquals("empresa@demo.com", detalle.getCorreo());
		assertEquals("3001234567", detalle.getCelular());
		assertEquals("Contacto Demo", detalle.getContacto());
		assertEquals("Descripcion completa", detalle.getDescripcion());
		assertEquals("logo.png", detalle.getLogo());
		assertEquals(1L, detalle.getEstadoId());
		assertEquals("Activo", detalle.getEstadoNombre());
		assertEquals(10L, detalle.getPersonaResponsableId());
		assertEquals("Ana Responsable", detalle.getPersonaResponsableNombre());
	}

	@Test
	void obtenerDetalle_usuarioEmpresaNoPuedeConsultarOtraEmpresa() {
		when(userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA")).thenReturn(false);
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(77L);

		assertThrows(AccessDeniedException.class, () -> empresaService.obtenerDetalle(88L));

		verifyNoInteractions(empresaRepository);
	}

	@Test
	void obtenerDetalle_empresaInexistenteRetornaNoEncontrada() {
		when(userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA")).thenReturn(true);
		when(empresaRepository.buscarDetallePorId(999L)).thenReturn(Optional.empty());

		RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class,
				() -> empresaService.obtenerDetalle(999L));

		assertEquals(404, exception.getStatusCode().value());
		verify(empresaRepository).buscarDetallePorId(999L);
	}

	private Empresa empresaCompleta(Long id) {
		return Empresa.builder()
			.id(id)
			.tipoIdentificacion(TipoIdentificacion.builder().id(5L).nombre("NIT").build())
			.identificacion("900123456")
			.nombre("Empresa Demo")
			.correo("empresa@demo.com")
			.celular("3001234567")
			.contacto("Contacto Demo")
			.descripcion("Descripcion completa")
			.logo("logo.png")
			.estado(Estado.builder().id(1L).nombre("Activo").build())
			.persona(Persona.builder().id(10L).nombre("Ana").apellido("Responsable").build())
			.build();
	}

}
