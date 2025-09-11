package com.coagronet.email.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.coagronet.verificationToken.TokenPurpose;
import com.coagronet.verificationToken.VerificationToken;
import com.coagronet.verificationToken.repositories.VerificationTokenRepository;

@Service
public class EmailVerificationService {

	private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

	private final VerificationTokenRepository verificationTokenRepository;

	private final JavaMailSender mailSender;

	@Value("${app.verification-url}")
	private String verificationUrl; // Debe apuntar al FRONT (ver secci?n 4)

	@Value("${app.reset-password-url}")
	private String resetPasswordUrl; // Debe apuntar al FRONT

	public EmailVerificationService(VerificationTokenRepository verificationTokenRepository,
			JavaMailSender mailSender) {
		this.verificationTokenRepository = verificationTokenRepository;
		this.mailSender = mailSender;
	}

	/* ================= TOKENS ================= */

	public String createVerificationToken(String email) {
		return createToken(email, TokenPurpose.VERIFY, 24);
	}

	public String createResetPasswordToken(String email) {
		return createToken(email, TokenPurpose.RESET, 1);
	}

	private String createToken(String email, TokenPurpose purpose, int hoursToExpire) {
		Optional<VerificationToken> existing = verificationTokenRepository.findByEmailAndPurpose(email, purpose);

		boolean aboutToExpire = existing.isPresent()
				&& existing.get().getExpiryDate().isBefore(LocalDateTime.now().plusMinutes(15));

		if (existing.isPresent() && !existing.get().isExpired() && !aboutToExpire) {
			logger.info("Reusing {} token for {}", purpose, email);
			return existing.get().getToken();
		}

		String token = java.util.UUID.randomUUID().toString();

		VerificationToken entity = existing.orElse(VerificationToken.builder().email(email).purpose(purpose).build());
		entity.setToken(token);
		entity.setExpiryDate(LocalDateTime.now().plusHours(hoursToExpire));

		verificationTokenRepository.save(entity);
		return token;
	}

	/** Verificaci?n de cuenta: valida consumo de token VERIFY y lo borra */
	public boolean consumeVerificationToken(String token) {
		return verificationTokenRepository.findByTokenAndPurpose(token, TokenPurpose.VERIFY)
			.filter(t -> !t.isExpired())
			.map(t -> {
				verificationTokenRepository.delete(t);
				return true;
			})
			.orElse(false);
	}

	/** Reset de password: devuelve email si token RESET es v?lido y lo borra */
	public String consumeResetPasswordToken(String token) {
		return verificationTokenRepository.findByTokenAndPurpose(token, TokenPurpose.RESET)
			.filter(t -> !t.isExpired())
			.map(t -> {
				verificationTokenRepository.delete(t);
				return t.getEmail();
			})
			.orElse(null);
	}

	/* ================= EMAILS ================= */

	public void sendVerificationEmail(String email, String token) {
		String subject = "Verify your account";
		String text = "Click to verify your account: " + verificationUrl + "?token=" + token;
		sendEmail(email, subject, text);
	}

	public void sendResetPasswordEmail(String email, String token) {
		String subject = "Reset your password";
		String text = "Click to reset your password: " + resetPasswordUrl + "?token=" + token;
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
		}
		catch (MailException e) {
			logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
		}
	}

}
