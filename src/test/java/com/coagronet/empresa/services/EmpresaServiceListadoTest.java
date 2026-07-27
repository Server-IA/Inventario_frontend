/*=============================================================================
 Nombre del archivo : EmpresaServiceListadoTest.java
 Descripcion        : Pruebas unitarias del listado filtrado de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion de pruebas para alcance por rol, empresa y filtros de la HU-043.2.                                                       |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.dtos.EmpresaListadoFiltroDTO;
import com.coagronet.empresa.dtos.EmpresaListadoResponseDTO;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.utils.UserRoleService;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceListadoTest {

	@Mock
	private EmpresaRepository empresaRepository;

	@Mock
	private UserEmpresaService userEmpresaService;

	@Mock
	private UserRoleService userRoleService;

	private EmpresaService empresaService;

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		empresaService = new EmpresaService(empresaRepository, userEmpresaService, userRoleService);
		pageable = PageRequest.of(0, 10);
	}

	@Test
	void listar_administradorSistemaConsultaTodasLasEmpresas() {
		when(userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA")).thenReturn(true);
		when(empresaRepository.buscarEmpresas(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
				eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(empresa(1L, "Empresa Uno"), empresa(2L, "Empresa Dos")), pageable, 2));

		EmpresaListadoResponseDTO response = empresaService.listar(new EmpresaListadoFiltroDTO(), pageable);

		assertEquals(2, response.getData().size());
		assertEquals(2, response.getHeader().getTotalElements());
		verify(userEmpresaService, never()).getEmpresaIdFromCurrentRequest();
	}

	@Test
	void listar_usuarioEmpresaSoloConsultaEmpresaDeSuJwt() {
		when(userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA")).thenReturn(false);
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(77L);
		EmpresaListadoFiltroDTO filtro = EmpresaListadoFiltroDTO.builder()
			.tipoIdentificacionId(5L)
			.identificacion(" 900 ")
			.nombre(" Demo ")
			.correo(" correo@demo.com ")
			.estadoId(1L)
			.build();
		when(empresaRepository.buscarEmpresas(77L, 5L, "900", "Demo", "correo@demo.com", 1L, pageable))
			.thenReturn(new PageImpl<>(List.of(empresa(77L, "Empresa Sesion")), pageable, 1));

		EmpresaListadoResponseDTO response = empresaService.listar(filtro, pageable);

		assertEquals(1, response.getData().size());
		assertEquals(77L, response.getData().getFirst().getId());
		verify(empresaRepository).buscarEmpresas(77L, 5L, "900", "Demo", "correo@demo.com", 1L, pageable);
	}

	@Test
	void listar_sinResultadosRetornaPaginaVacia() {
		when(userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA")).thenReturn(true);
		when(empresaRepository.buscarEmpresas(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
				any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		EmpresaListadoResponseDTO response = empresaService.listar(new EmpresaListadoFiltroDTO(), pageable);

		assertTrue(response.getData().isEmpty());
		assertTrue(response.getHeader().isEmpty());
		assertEquals(0, response.getHeader().getTotalElements());
	}

	private Empresa empresa(Long id, String nombre) {
		return Empresa.builder()
			.id(id)
			.tipoIdentificacion(TipoIdentificacion.builder().id(5L).nombre("NIT").build())
			.identificacion("900" + id)
			.nombre(nombre)
			.correo("empresa" + id + "@correo.com")
			.estado(Estado.builder().id(1L).nombre("Activo").build())
			.build();
	}

}
