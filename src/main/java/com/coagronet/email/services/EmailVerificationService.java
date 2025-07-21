package com.coagronet.email.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.coagronet.verificationToken.VerificationToken;
import com.coagronet.verificationToken.repositories.VerificationTokenRepository;

@Service
public class EmailVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

    private final VerificationTokenRepository verificationTokenRepository;
    private final JavaMailSender mailSender;

    @Value("${app.verification-url}")
    private String verificationUrl;

    @Value("${app.reset-password-url}")
    private String resetPasswordUrl;

    public EmailVerificationService(VerificationTokenRepository verificationTokenRepository,
            JavaMailSender mailSender) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailSender = mailSender;
    }

    public String createVerificationToken(String email) {

        Optional<VerificationToken> existing = verificationTokenRepository.findByEmail(email);

        // ⬇️ NUEVO chequeo de “casi expirado”
        boolean aboutToExpire = existing.isPresent() &&
                existing.get().getExpiryDate()
                        .isBefore(LocalDateTime.now().plusMinutes(15));

        // Si existe, no está expirado y aún le queda más de 15 min de vida → reutilizar
        if (existing.isPresent() && !existing.get().isExpired() && !aboutToExpire) {
            return existing.get().getToken();
        }

        // Caso contrario: crear o renovar el token
        String token = UUID.randomUUID().toString();

        VerificationToken entity = existing.orElse(
                VerificationToken.builder().email(email).build());

        entity.setToken(token);
        entity.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(entity);
        return token;
    }

    public boolean validateToken(String token) {
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(token);

        return tokenOptional
                .filter(t -> !t.isExpired())
                .map(t -> {
                    verificationTokenRepository.delete(t);
                    logger.info("Token validated and deleted for email: {}", t.getEmail());
                    return true;
                })
                .orElse(false);
    }

    public String getEmailAndInvalidateToken(String token) {
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(token);

        return tokenOptional
                .filter(t -> !t.isExpired())
                .map(t -> {
                    verificationTokenRepository.delete(t);
                    logger.info("Token used and deleted for email: {}", t.getEmail());
                    return t.getEmail();
                })
                .orElse(null);
    }

    public void sendVerificationEmail(String email, String token) {
        String subject = "Verify your account";
        String text = "Click the link to verify your account: " + verificationUrl + "?token=" + token;
        sendEmail(email, subject, text);
    }

    public void sendResetPasswordEmail(String email, String token) {
        String subject = "Reset your password";
        String text = "Click the link to reset your password: " + resetPasswordUrl + "?token=" + token;
        sendEmail(email, subject, text);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            logger.info("Email sent to {} with subject: {}", to, subject);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            // Podrías lanzar una excepción customizada si quieres que el error suba
        }
    }
}
