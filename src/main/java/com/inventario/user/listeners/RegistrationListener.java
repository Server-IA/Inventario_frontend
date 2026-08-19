package com.inventario.user.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.inventario.email.services.EmailVerificationService;
import com.inventario.user.events.OnRegistrationCompleteEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegistrationListener {

	private final EmailVerificationService emailService;

	@Async
	@EventListener
	public void handleRegistrationComplete(OnRegistrationCompleteEvent event) {
		String email = event.getUser().getUsername();
		String token = emailService.createVerificationToken(email);
		emailService.sendVerificationEmail(email, token, event.getFallbackLanguageTag());
	}

}
