package com.coagronet.usuariorol.utils;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuariorol.UsuarioRol;
import com.coagronet.usuariorol.repositories.UsuarioRolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioRolContratoService {

	private static final Logger logger = LoggerFactory.getLogger(UsuarioRolContratoService.class);

	private static final Long ESTADO_ACTIVO_ID = 1L;
	private static final Long ESTADO_INACTIVO_ID = 2L;

	private final UsuarioRolRepository usuarioRolRepository;
	private final EstadoRepository estadoRepository;
	private final UserRepository userRepository;

	@Transactional
	public void procesarContratos() {
		OffsetDateTime now = OffsetDateTime.now();

		int activados = activarPendientes(now);
		int inactivados = inactivarExpirados(now);

		logger.info("Procesamiento de contratos completado: {} activados, {} inactivados", activados, inactivados);
	}

	private int activarPendientes(OffsetDateTime fechaActual) {
		List<UsuarioRol> pendientes = usuarioRolRepository
				.findByEstadoInactivoYFechaActivacion(ESTADO_INACTIVO_ID, fechaActual);

		if (pendientes.isEmpty()) {
			return 0;
		}

		Estado estadoActivo = estadoRepository.findById(ESTADO_ACTIVO_ID)
				.orElseThrow(() -> new IllegalStateException("Estado ACTIVO no encontrado"));

		for (UsuarioRol ur : pendientes) {
			ur.setEstado(estadoActivo);
			ur.setUpdatedAt(OffsetDateTime.now());
			usuarioRolRepository.save(ur);
			logger.debug("UsuarioRol id={} activado por fecha de inicio contrato", ur.getId());
		}

		return pendientes.size();
	}

	private int inactivarExpirados(OffsetDateTime fechaActual) {
		List<UsuarioRol> expirados = usuarioRolRepository
				.findByEstadoActivoYFechaFinalizacionPasada(ESTADO_ACTIVO_ID, fechaActual);

		if (expirados.isEmpty()) {
			return 0;
		}

		Estado estadoInactivo = estadoRepository.findById(ESTADO_INACTIVO_ID)
				.orElseThrow(() -> new IllegalStateException("Estado INACTIVO no encontrado"));

		for (UsuarioRol ur : expirados) {
			ur.setEstado(estadoInactivo);
			ur.setUpdatedAt(OffsetDateTime.now());
			usuarioRolRepository.save(ur);

			User user = ur.getUser();
			boolean eraPreferida = user.getPreferredEmpresaId() != null
					&& user.getPreferredRolId() != null
					&& user.getPreferredEmpresaId().equals(ur.getEmpresa().getId())
					&& user.getPreferredRolId().equals(ur.getRol().getId());

			if (eraPreferida) {
				user.setPreferredEmpresaId(null);
				user.setPreferredRolId(null);

				List<UsuarioRol> otrasActivas = usuarioRolRepository
						.findActivasByUserId(ESTADO_ACTIVO_ID, user.getId());
				UsuarioRol nuevaPreferida = otrasActivas.stream()
						.filter(a -> !a.getId().equals(ur.getId()))
						.findFirst()
						.orElse(null);

				if (nuevaPreferida != null) {
					user.setPreferredEmpresaId(nuevaPreferida.getEmpresa().getId());
					user.setPreferredRolId(nuevaPreferida.getRol().getId());
					logger.debug("Usuario id={}: preferencia cambiada a empresaId={}, rolId={}",
							user.getId(), nuevaPreferida.getEmpresa().getId(), nuevaPreferida.getRol().getId());
				}

				userRepository.save(user);
				logger.debug("UsuarioRol id={} inactivado - preferencia removida del usuario id={}", ur.getId(), user.getId());
			}

			logger.debug("UsuarioRol id={} inactivado por fecha de fin contrato", ur.getId());
		}

		return expirados.size();
	}
}
