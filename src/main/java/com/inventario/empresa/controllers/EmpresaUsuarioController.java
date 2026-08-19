/*=============================================================================
 Nombre del archivo : EmpresaUsuarioController.java
 Descripcion        : Controlador REST para la gestión de empresas y usuarios.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-24 | 0.4.0   | JUAN JOSE CASTRO     | Asignación de los atributos |
 |            |         |                      | de auditoría createdAt      |
 |            |         |                      | (usando Instant.now()) y    |
 |            |         |                      | createdBy (pasando la       |
 |            |         |                      | entidad User) durante la    |
 |            |         |                      | creación del registro       |
 |            |         |                      | inicial en EmpresaRol.      |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.inventario.empresa.controllers;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.empresa.Empresa;
import com.inventario.empresa.dtos.EmpresaDTO;
import com.inventario.empresa.mappers.EmpresaMapper;
import com.inventario.empresa.services.EmpresaService;
import com.inventario.empresarol.EmpresaRol;
import com.inventario.empresarol.repositories.EmpresaRolRepository;
import com.inventario.estado.Estado;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.infrastructure.security.JwtUtil;
import com.inventario.modulo.Modulo;
import com.inventario.modulo.enums.AlcanceModulo;
import com.inventario.modulo.repositories.ModuloRepository;
import com.inventario.moduloempresa.ModuloEmpresa;
import com.inventario.moduloempresa.repositories.ModuloEmpresaRepository;
import com.inventario.permiso.Permiso;
import com.inventario.permiso.repositories.PermisoRepository;
import com.inventario.rol.Rol;
import com.inventario.rol.repositories.RolRepository;
import com.inventario.rolpermiso.RolPermiso;
import com.inventario.rolpermiso.repositories.RolPermisoRepository;
import com.inventario.user.User;
import com.inventario.user.repositories.UserRepository;
import com.inventario.usuarioEstado.UsuarioEstado;
import com.inventario.usuarioEstado.repositories.UsuarioEstadoRepository;
import com.inventario.usuariorol.UsuarioRol;
import com.inventario.usuariorol.repositories.UsuarioRolRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaUsuarioController {

	private final EmpresaService empresaService;

	private final JwtUtil jwtUtil;

	private final UserRepository userRepository;

	private final UsuarioRolRepository usuarioRolRepository;

	private final ModuloRepository moduloRepository;

	private final EstadoRepository estadoRepository;

	private final ModuloEmpresaRepository moduloEmpresaRepository;

	private final RolRepository rolRepository;

	private final EmpresaRolRepository empresaRolRepository;

	private final PermisoRepository permisoRepository;

	private final RolPermisoRepository rolPermisoRepository;

	private final UsuarioEstadoRepository usuarioEstadoRepository;

	@Transactional
	@PostMapping("/empresa-usuario")
	public ResponseEntity<Map<String, Integer>> createEmpresa(@RequestBody EmpresaDTO empresaDTO,
			@RequestHeader("Authorization") String authorizationHeader) {

		String token = authorizationHeader.replace("Bearer ", "").trim();
		String username = jwtUtil.extractUsername(token);

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		Empresa empresa = EmpresaMapper.INSTANCE.toEmpresa(empresaDTO);
		empresa.setPersona(user.getPersona());

		// 1. Guardar la entidad Empresa
		Empresa savedEmpresa = empresaService.save(empresa);

		// ==========================================
		// INICIO DE INICIALIZACIÓN DE PERMISOS BASE
		// ==========================================

		List<Modulo> modulosBase = moduloRepository.findByAlcanceAndRequeridoTrue(AlcanceModulo.EMPRESA);
		Estado estadoActivo = estadoRepository.getReferenceById(1L);
		List<ModuloEmpresa> modulosEmpresa = modulosBase.stream().map(modulo -> {

			ModuloEmpresa me = new ModuloEmpresa();
			me.setModulo(modulo);
			me.setEmpresa(savedEmpresa);
			// Asegúrate de usar el objeto o ID correcto según tu entidad Estado
			me.setEstado(estadoActivo);
			return me;
		}).toList();
		moduloEmpresaRepository.saveAll(modulosEmpresa);

		Rol rolAdmin = rolRepository.findById(2L)
				.orElseThrow(() -> new RuntimeException("Rol Administrador no encontrado"));

		EmpresaRol empresaRol = new EmpresaRol();
		empresaRol.setEmpresa(savedEmpresa);
		empresaRol.setRol(rolAdmin);
		empresaRol.setEstado(estadoActivo);
		empresaRol.setCreatedAt(Instant.now());
		empresaRol.setCreatedBy(user);
		EmpresaRol savedEmpresaRol = empresaRolRepository.save(empresaRol);

		List<Long> modulosIds = modulosBase.stream().map(Modulo::getId).toList();

		if (!modulosIds.isEmpty()) {
			List<Permiso> permisosAdmin = permisoRepository.findByModuloIdInAndAdminEmpresaTrue(modulosIds);

			List<RolPermiso> rolPermisos = permisosAdmin.stream().map(permiso -> {
				RolPermiso rp = new RolPermiso();
				rp.setEmpresaRol(savedEmpresaRol);
				rp.setPermiso(permiso);
				rp.setEstado(estadoActivo);
				rp.setCreatedAt(Instant.now());

				return rp;
			}).toList();
			rolPermisoRepository.saveAll(rolPermisos);
		}

		// ==========================================
		// FIN DE INICIALIZACIÓN
		// ==========================================

		user.setUsuarioEstado(usuarioEstadoRepository.getReferenceById(UsuarioEstado.ID_ACTIVADO_CON_EMPRESA));
		userRepository.save(user);

		UsuarioRol usuarioRol = usuarioRolRepository.findByUser(user);
		usuarioRol.setEmpresa(savedEmpresa);
		usuarioRolRepository.save(usuarioRol);

		Map<String, Integer> response = new HashMap<>();
		response.put("usuarioEstado", user.getUsuarioEstado().getId().intValue());

		return ResponseEntity.ok(response);
	}

}
