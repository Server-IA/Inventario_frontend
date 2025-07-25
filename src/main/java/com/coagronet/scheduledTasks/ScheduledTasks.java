package com.coagronet.scheduledTasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.coagronet.verificationToken.services.TokenCleanupService;

@Component
public class ScheduledTasks {

	private final TokenCleanupService tokenCleanupService;

	public ScheduledTasks(TokenCleanupService tokenCleanupService) {
		this.tokenCleanupService = tokenCleanupService;
	}

	@Scheduled(fixedRate = 3600000) // Ejecuta cada hora
	public void cleanUpExpiredTokens() {
		tokenCleanupService.deleteExpiredTokens();
	}

}
