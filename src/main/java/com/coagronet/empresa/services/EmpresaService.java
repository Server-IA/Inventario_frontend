/*=============================================================================
 Nombre del archivo : EmpresaService.java
 Descripcion        : Servicio de negocio para la gestion de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2024-08-16 | 1.0.0   | yourusername         | Creacion del archivo.                                                                                                              |
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Implementacion de listado filtrado y alcance por rol y empresa para la HU-043.2.                                                   |
 | 2026-07-27 | 1.1.1   | JUAN DIAZ            | Normalizacion de filtros de texto ausentes como cadenas vacias para asegurar su tipado correcto en PostgreSQL.                     |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.dtos.EmpresaListadoFiltroDTO;
import com.coagronet.empresa.dtos.EmpresaListadoItemDTO;
import com.coagronet.empresa.dtos.EmpresaListadoResponseDTO;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.utils.Constantes;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.utils.UserRoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

	private static final String ROLE_ADMINISTRADOR_SISTEMA = "ROLE_ADMINISTRADOR_SISTEMA";

	@Value("${path.logos}")
	private String pathLogos;

	@Value("${path.logo.empresa}")
	private String pathLogoCompany;

	private final EmpresaRepository empresaRepository;

	private final UserEmpresaService userEmpresaService;

	private final UserRoleService userRoleService;

	@Transactional(readOnly = true)
	public EmpresaListadoResponseDTO listar(EmpresaListadoFiltroDTO filtro, Pageable pageable) {
		Long empresaId = userRoleService.hasRoleInAuthentication(ROLE_ADMINISTRADOR_SISTEMA)
				? null
				: userEmpresaService.getEmpresaIdFromCurrentRequest();

		Page<Empresa> pagina = empresaRepository.buscarEmpresas(
				empresaId,
				filtro.getTipoIdentificacionId(),
				normalizarFiltro(filtro.getIdentificacion()),
				normalizarFiltro(filtro.getNombre()),
				normalizarFiltro(filtro.getCorreo()),
				filtro.getEstadoId(),
				pageable);

		return EmpresaListadoResponseDTO.builder()
			.header(EmpresaListadoResponseDTO.Paginacion.builder()
				.totalElements(pagina.getTotalElements())
				.totalPages(pagina.getTotalPages())
				.size(pagina.getSize())
				.number(pagina.getNumber())
				.first(pagina.isFirst())
				.last(pagina.isLast())
				.numberOfElements(pagina.getNumberOfElements())
				.empty(pagina.isEmpty())
				.build())
			.data(pagina.getContent().stream().map(this::toListadoItem).toList())
			.build();
	}

	public Page<Empresa> getAllEmpresas(Pageable pageable) {
		return empresaRepository.findByEstadoNot(2, pageable);
	}

	public Empresa getEmpresaById(Long id) {
		// Asegúrate de que también se filtren por estado aquí si es necesario
		Empresa empresa = empresaRepository.findById(id).orElse(null);

		return (empresa != null && empresa.getEstado().getId() != 2) ? empresa : null;
	}

	public Empresa save(Empresa empresa) {
		return empresaRepository.save(empresa);
	}

	public Empresa update(Empresa empresa) {
		return empresaRepository.save(empresa);
	}

	public void deleteEmpresa(Long id) {
		Empresa empresa = empresaRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Persona not found with id: " + id));
		empresa.getEstado().setId(Constantes.ESTADO_INACTIVO);
		empresaRepository.save(empresa);
	}

	public String getLogoHashByEmpresaId(Long empresaId) {
		return empresaRepository.findLogoHashByEmpresaId(empresaId);
	}

	public String findLogoByHash(String hash) {
		return empresaRepository.findLogoByHash(hash);
	}

	public void subirLogoDesdeEmpresaLogueada(MultipartFile file) {
		try {
			if (!file.getContentType().equalsIgnoreCase("image/png")) {
				throw new IllegalArgumentException("Solo se permiten archivos PNG.");
			}

			Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
			String originalFilename = file.getOriginalFilename(); // ej: logo_empresa.png

			// Hash del nombre base (sin extensión)
			String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
			String hash = generarHash(baseName);

			// Ruta destino: <PATH_LOGOS>/<PATH_LOGO_COMPANY>/<empresaId>/
			Path rutaEmpresa = Paths.get(pathLogos, pathLogoCompany, empresaId.toString());
			Files.createDirectories(rutaEmpresa);

			// Nuevo código de develop: Eliminar logo anterior si existe
			String hashAnterior = getLogoHashByEmpresaId(empresaId);
			if (hashAnterior != null) {
				String logoAnterior = findLogoByHash(hashAnterior);
				if (logoAnterior != null) {
					Path rutaLogoAnterior = rutaEmpresa.resolve(logoAnterior);
					System.out.println(logoAnterior);
					if (Files.exists(rutaLogoAnterior)) {
						Files.delete(rutaLogoAnterior);
					}
				}
			}

			// Guardar archivo
			Path rutaLogoFinal = rutaEmpresa.resolve(originalFilename);
			file.transferTo(rutaLogoFinal);

			// Actualizar en la base de datos
			Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
			empresa.setLogo(originalFilename); // ej: "logo_empresa.png"
			empresa.setLogoHash(hash);
			empresaRepository.save(empresa);

		}
		catch (IOException e) {
			throw new RuntimeException("Error al guardar el logo", e);
		}
	}

	private String generarHash(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hashBytes);
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No se pudo generar hash", e);
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private EmpresaListadoItemDTO toListadoItem(Empresa empresa) {
		return EmpresaListadoItemDTO.builder()
			.id(empresa.getId())
			.tipoIdentificacionId(empresa.getTipoIdentificacion().getId())
			.tipoIdentificacionNombre(empresa.getTipoIdentificacion().getNombre())
			.identificacion(empresa.getIdentificacion())
			.nombre(empresa.getNombre())
			.correo(empresa.getCorreo())
			.estadoId(empresa.getEstado() != null ? empresa.getEstado().getId() : null)
			.estadoNombre(empresa.getEstado() != null ? empresa.getEstado().getNombre() : null)
			.build();
	}

	private String normalizarFiltro(String valor) {
		if (valor == null || valor.isBlank()) {
			return "";
		}
		return valor.trim();
	}

}
