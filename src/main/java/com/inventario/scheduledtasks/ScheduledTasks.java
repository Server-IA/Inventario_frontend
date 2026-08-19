package com.inventario.scheduledtasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.inventario.usuariorol.utils.UsuarioRolContratoService;
import com.inventario.verificationToken.services.TokenCleanupService;

@Component
public class ScheduledTasks {

	private final TokenCleanupService tokenCleanupService;
	private final UsuarioRolContratoService usuarioRolContratoService;

	public ScheduledTasks(TokenCleanupService tokenCleanupService,
			UsuarioRolContratoService usuarioRolContratoService) {
		this.tokenCleanupService = tokenCleanupService;
		this.usuarioRolContratoService = usuarioRolContratoService;
	}

	@Scheduled(fixedRate = 3600000)
	public void cleanUpExpiredTokens() {
		tokenCleanupService.deleteExpiredTokens();
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void procesarContratosUsuarioRol() {
		usuarioRolContratoService.procesarContratos();
	}

}
