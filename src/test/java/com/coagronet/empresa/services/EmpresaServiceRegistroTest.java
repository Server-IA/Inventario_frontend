/*=============================================================================
 Nombre del archivo : EmpresaServiceRegistroTest.java
 Descripcion        : Pruebas unitarias del registro de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion de pruebas para los criterios de aceptacion de la HU-043.1.                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.dtos.EmpresaRegistroRequestDTO;
import com.coagronet.empresa.dtos.EmpresaRegistroResponseDTO;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.exceptionHandler.custom.RecursoDuplicadoException;
import com.coagronet.persona.Persona;
import com.coagronet.persona.repositories.PersonaRepository;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;
import com.coagronet.tipoIdentificacion.repositories.TipoIdentificacionRepository;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.utils.UserRoleService;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceRegistroTest {

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

	@TempDir
	private Path tempDir;

	private EmpresaService empresaService;

	private Estado estadoActivo;

	private TipoIdentificacion tipoIdentificacionActivo;

	private Persona personaActiva;

	@BeforeEach
	void setUp() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("empresa.identificacion.existente", Locale.getDefault(), "Identificacion duplicada");
		messageSource.addMessage("empresa.correo.existente", Locale.getDefault(), "Correo duplicado");
		messageSource.addMessage("empresa.tipo-identificacion.invalid", Locale.getDefault(),
				"Tipo de identificacion invalido");
		messageSource.addMessage("empresa.persona-responsable.invalid", Locale.getDefault(),
				"Persona responsable invalida");
		messageSource.addMessage("empresa.estado-activo.invalid", Locale.getDefault(), "Estado activo invalido");
		messageSource.addMessage("empresa.logo.formato-invalido", Locale.getDefault(), "Formato de logo invalido");
		messageSource.addMessage("empresa.logo.tamano-excedido", Locale.getDefault(), "Logo superior a {0} bytes");
		messageSource.addMessage("empresa.logo.almacenamiento-error", Locale.getDefault(), "Error almacenando logo");

		empresaService = new EmpresaService(empresaRepository, userEmpresaService, userRoleService,
				tipoIdentificacionRepository, personaRepository, estadoRepository, messageSource);
		ReflectionTestUtils.setField(empresaService, "pathLogos", tempDir.toString());
		ReflectionTestUtils.setField(empresaService, "pathLogoCompany", "empresas");
		ReflectionTestUtils.setField(empresaService, "logoMaxSizeBytes", 2_097_152L);

		estadoActivo = Estado.builder().id(1L).nombre("Activo").build();
		tipoIdentificacionActivo = TipoIdentificacion.builder().id(7L).estado(estadoActivo).build();
		personaActiva = Persona.builder().id(15L).estado(estadoActivo).build();
	}

	@Test
	void registrar_creaEmpresaActivaConDatosNormalizados() {
		prepararRegistroExitoso();

		EmpresaRegistroResponseDTO response = empresaService.registrar(requestValido(), null);

		assertEquals(99L, response.getId());
		assertEquals("900123456", response.getIdentificacion());
		assertEquals("empresa@correo.com", response.getCorreo());
		assertEquals(1L, response.getEstadoId());
		assertEquals(15L, response.getPersonaId());
		assertFalse(response.isLogoCargado());
		assertNull(response.getAdvertenciaLogo());
		verify(empresaRepository).saveAndFlush(any(Empresa.class));
	}

	@Test
	void registrar_bloqueaIdentificacionDuplicada() {
		when(empresaRepository.existsByIdentificacionIgnoreCase("900123456")).thenReturn(true);

		assertThrows(RecursoDuplicadoException.class, () -> empresaService.registrar(requestValido(), null));

		verify(empresaRepository, never()).saveAndFlush(any(Empresa.class));
	}

	@Test
	void registrar_bloqueaCorreoDuplicado() {
		when(empresaRepository.existsByCorreoIgnoreCase("empresa@correo.com")).thenReturn(true);

		assertThrows(RecursoDuplicadoException.class, () -> empresaService.registrar(requestValido(), null));

		verify(empresaRepository, never()).saveAndFlush(any(Empresa.class));
	}

	@Test
	void registrar_rechazaTipoIdentificacionInactivo() {
		TipoIdentificacion tipoInactivo = TipoIdentificacion.builder()
			.id(7L)
			.estado(Estado.builder().id(2L).build())
			.build();
		when(tipoIdentificacionRepository.findById(7L)).thenReturn(Optional.of(tipoInactivo));

		assertThrows(BadRequestException.class, () -> empresaService.registrar(requestValido(), null));

		verify(empresaRepository, never()).saveAndFlush(any(Empresa.class));
	}

	@Test
	void registrar_rechazaPersonaResponsableInactiva() {
		when(tipoIdentificacionRepository.findById(7L)).thenReturn(Optional.of(tipoIdentificacionActivo));
		Persona personaInactiva = Persona.builder().id(15L).estado(Estado.builder().id(2L).build()).build();
		when(personaRepository.findById(15L)).thenReturn(Optional.of(personaInactiva));

		assertThrows(BadRequestException.class, () -> empresaService.registrar(requestValido(), null));

		verify(empresaRepository, never()).saveAndFlush(any(Empresa.class));
	}

	@Test
	void registrar_rechazaLogoNoPngSinBloquearEmpresa() {
		prepararRegistroExitoso();
		MockMultipartFile logo = new MockMultipartFile("logo", "logo.jpg", "image/jpeg", new byte[] { 1, 2, 3 });

		EmpresaRegistroResponseDTO response = empresaService.registrar(requestValido(), logo);

		assertEquals(99L, response.getId());
		assertFalse(response.isLogoCargado());
		assertEquals("Formato de logo invalido", response.getAdvertenciaLogo());
		verify(empresaRepository).saveAndFlush(any(Empresa.class));
	}

	@Test
	void registrar_rechazaLogoGrandeSinBloquearEmpresa() {
		prepararRegistroExitoso();
		ReflectionTestUtils.setField(empresaService, "logoMaxSizeBytes", 2L);
		MockMultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", new byte[] { 1, 2, 3 });

		EmpresaRegistroResponseDTO response = empresaService.registrar(requestValido(), logo);

		assertEquals(99L, response.getId());
		assertFalse(response.isLogoCargado());
		assertNotNull(response.getAdvertenciaLogo());
		verify(empresaRepository).saveAndFlush(any(Empresa.class));
	}

	@Test
	void registrar_guardaLogoPngValido() {
		prepararRegistroExitoso();
		MockMultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", new byte[] { 1, 2, 3 });

		EmpresaRegistroResponseDTO response = empresaService.registrar(requestValido(), logo);

		assertTrue(response.isLogoCargado());
		assertNotNull(response.getLogo());
		assertTrue(Files.exists(tempDir.resolve("empresas").resolve("99").resolve(response.getLogo())));
		verify(empresaRepository, org.mockito.Mockito.times(2)).saveAndFlush(any(Empresa.class));
	}

	private void prepararRegistroExitoso() {
		when(tipoIdentificacionRepository.findById(7L)).thenReturn(Optional.of(tipoIdentificacionActivo));
		when(personaRepository.findById(15L)).thenReturn(Optional.of(personaActiva));
		when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoActivo));
		when(empresaRepository.saveAndFlush(any(Empresa.class))).thenAnswer(invocation -> {
			Empresa empresa = invocation.getArgument(0);
			if (empresa.getId() == null) {
				empresa.setId(99L);
			}
			return empresa;
		});
	}

	private EmpresaRegistroRequestDTO requestValido() {
		return EmpresaRegistroRequestDTO.builder()
			.tipoIdentificacionId(7L)
			.identificacion(" 900123456 ")
			.nombre(" Empresa Demo ")
			.correo(" EMPRESA@CORREO.COM ")
			.celular("3001234567")
			.contacto("Contacto Demo")
			.descripcion("Empresa para pruebas")
			.personaId(15L)
			.build();
	}

}
