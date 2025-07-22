package com.coagronet.user.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.coagronet.email.services.EmailVerificationService;
import com.coagronet.user.events.OnRegistrationCompleteEvent;

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
        emailService.sendVerificationEmail(email, token);
    }

}
