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
import org.springframework.web.multipart.MultipartFile;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.utils.Constantes;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

	@Value("${path.logos}")
	private String pathLogos;

	@Value("${path.logo.empresa}")
	private String pathLogoCompany;

	private final EmpresaRepository empresaRepository;

	private final UserEmpresaService userEmpresaService;

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

}
