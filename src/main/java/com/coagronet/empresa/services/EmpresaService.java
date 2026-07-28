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
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Implementacion de registro, validaciones de unicidad, responsable y carga opcional de logo para la HU-043.1.                      |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.services;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.dtos.EmpresaListadoFiltroDTO;
import com.coagronet.empresa.dtos.EmpresaListadoItemDTO;
import com.coagronet.empresa.dtos.EmpresaListadoResponseDTO;
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
import com.coagronet.utils.Constantes;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.utils.UserRoleService;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

	private static final String ROLE_ADMINISTRADOR_SISTEMA = "ROLE_ADMINISTRADOR_SISTEMA";

	@Value("${path.logos}")
	private String pathLogos;

	@Value("${path.logo.empresa}")
	private String pathLogoCompany;

	@Value("${empresa.logo.max-size-bytes:2097152}")
	private long logoMaxSizeBytes;

	private final EmpresaRepository empresaRepository;

	private final UserEmpresaService userEmpresaService;

	private final UserRoleService userRoleService;

	private final TipoIdentificacionRepository tipoIdentificacionRepository;

	private final PersonaRepository personaRepository;

	private final EstadoRepository estadoRepository;

	private final MessageSource messageSource;

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

	@Transactional
	public EmpresaRegistroResponseDTO registrar(EmpresaRegistroRequestDTO request, MultipartFile logo) {
		String identificacion = request.getIdentificacion().trim();
		String correo = request.getCorreo().trim().toLowerCase(Locale.ROOT);

		validarUnicidad(identificacion, correo);

		TipoIdentificacion tipoIdentificacion = obtenerTipoIdentificacionActivo(request.getTipoIdentificacionId());
		Persona personaResponsable = obtenerPersonaResponsableActiva(request.getPersonaId());
		Estado estadoActivo = estadoRepository.findById(EstadoConstantes.ESTADO_GENERAL_ACTIVO)
			.orElseThrow(() -> new BadRequestException(mensaje("empresa.estado-activo.invalid")));

		ValidacionLogo validacionLogo = validarLogo(logo);

		Empresa empresa = Empresa.builder()
			.tipoIdentificacion(tipoIdentificacion)
			.identificacion(identificacion)
			.nombre(request.getNombre().trim())
			.persona(personaResponsable)
			.descripcion(normalizarOpcional(request.getDescripcion()))
			.estado(estadoActivo)
			.celular(normalizarOpcional(request.getCelular()))
			.correo(correo)
			.contacto(normalizarOpcional(request.getContacto()))
			.build();

		Empresa empresaGuardada = empresaRepository.saveAndFlush(empresa);
		LogoGuardado logoGuardado = null;

		try {
			if (validacionLogo.debeGuardarse()) {
				logoGuardado = guardarLogo(empresaGuardada.getId(), logo);
				empresaGuardada.setLogo(logoGuardado.nombreArchivo());
				empresaGuardada.setLogoHash(logoGuardado.hash());
				empresaGuardada = empresaRepository.saveAndFlush(empresaGuardada);
			}
		}
		catch (RuntimeException ex) {
			eliminarLogoSilenciosamente(logoGuardado);
			throw ex;
		}

		return construirRespuesta(empresaGuardada, validacionLogo);
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

	private void validarUnicidad(String identificacion, String correo) {
		if (empresaRepository.existsByIdentificacionIgnoreCase(identificacion)) {
			throw new RecursoDuplicadoException(mensaje("empresa.identificacion.existente"));
		}
		if (empresaRepository.existsByCorreoIgnoreCase(correo)) {
			throw new RecursoDuplicadoException(mensaje("empresa.correo.existente"));
		}
	}

	private TipoIdentificacion obtenerTipoIdentificacionActivo(Long tipoIdentificacionId) {
		return tipoIdentificacionRepository.findById(tipoIdentificacionId)
			.filter(tipo -> tipo.getEstado() != null
					&& EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(tipo.getEstado().getId()))
			.orElseThrow(() -> new BadRequestException(mensaje("empresa.tipo-identificacion.invalid")));
	}

	private Persona obtenerPersonaResponsableActiva(Long personaId) {
		return personaRepository.findById(personaId)
			.filter(persona -> persona.getEstado() != null
					&& EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(persona.getEstado().getId()))
			.orElseThrow(() -> new BadRequestException(mensaje("empresa.persona-responsable.invalid")));
	}

	private ValidacionLogo validarLogo(MultipartFile logo) {
		if (logo == null || logo.isEmpty()) {
			return new ValidacionLogo(false, null);
		}

		if (!MediaType.IMAGE_PNG_VALUE.equalsIgnoreCase(logo.getContentType())) {
			return new ValidacionLogo(false, mensaje("empresa.logo.formato-invalido"));
		}

		if (logo.getSize() > logoMaxSizeBytes) {
			return new ValidacionLogo(false, mensaje("empresa.logo.tamano-excedido", logoMaxSizeBytes));
		}

		return new ValidacionLogo(true, null);
	}

	private LogoGuardado guardarLogo(Long empresaId, MultipartFile logo) {
		String hash = generarHash(UUID.randomUUID().toString());
		String nombreArchivo = "logo-" + hash.substring(0, 16) + ".png";
		Path directorioEmpresa = Paths.get(pathLogos, pathLogoCompany, empresaId.toString()).normalize();
		Path rutaLogo = directorioEmpresa.resolve(nombreArchivo).normalize();

		try {
			Files.createDirectories(directorioEmpresa);
			logo.transferTo(rutaLogo);
			return new LogoGuardado(nombreArchivo, hash, rutaLogo);
		}
		catch (IOException ex) {
			try {
				Files.deleteIfExists(rutaLogo);
			}
			catch (IOException cleanupException) {
				ex.addSuppressed(cleanupException);
			}
			throw new UncheckedIOException(mensaje("empresa.logo.almacenamiento-error"), ex);
		}
	}

	private void eliminarLogoSilenciosamente(LogoGuardado logoGuardado) {
		if (logoGuardado == null) {
			return;
		}
		try {
			Files.deleteIfExists(logoGuardado.ruta());
		}
		catch (IOException ignored) {
			// La excepcion original conserva la causa funcional de la transaccion.
		}
	}

	private EmpresaRegistroResponseDTO construirRespuesta(Empresa empresa, ValidacionLogo validacionLogo) {
		return EmpresaRegistroResponseDTO.builder()
			.id(empresa.getId())
			.tipoIdentificacionId(empresa.getTipoIdentificacion().getId())
			.identificacion(empresa.getIdentificacion())
			.nombre(empresa.getNombre())
			.correo(empresa.getCorreo())
			.celular(empresa.getCelular())
			.contacto(empresa.getContacto())
			.descripcion(empresa.getDescripcion())
			.personaId(empresa.getPersona().getId())
			.estadoId(empresa.getEstado().getId())
			.logo(empresa.getLogo())
			.logoCargado(empresa.getLogo() != null)
			.advertenciaLogo(validacionLogo.advertencia())
			.build();
	}

	private String normalizarOpcional(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}

	private String mensaje(String codigo, Object... argumentos) {
		return messageSource.getMessage(codigo, argumentos, codigo, LocaleContextHolder.getLocale());
	}

	private record ValidacionLogo(boolean debeGuardarse, String advertencia) {
	}

	private record LogoGuardado(String nombreArchivo, String hash, Path ruta) {
	}

}
