package com.inventario.email.services;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.inventario.infrastructure.i18n.LocaleResolutionService;
import com.inventario.user.LanguagePreference;
import com.inventario.user.repositories.UserRepository;
import com.inventario.verificationToken.TokenPurpose;
import com.inventario.verificationToken.VerificationToken;
import com.inventario.verificationToken.repositories.VerificationTokenRepository;

@Service
public class EmailVerificationService {

	private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

	private final VerificationTokenRepository verificationTokenRepository;
	private final JavaMailSender mailSender;
	private final MessageSource messageSource;
	private final UserRepository userRepository;
	private final LocaleResolutionService localeResolutionService;

	@Value("${app.verification-url}")
	private String verificationUrl;

	@Value("${app.reset-password-url}")
	private String resetPasswordUrl;

	public EmailVerificationService(
			VerificationTokenRepository verificationTokenRepository,
			JavaMailSender mailSender,
			MessageSource messageSource,
			UserRepository userRepository,
			LocaleResolutionService localeResolutionService) {
		this.verificationTokenRepository = verificationTokenRepository;
		this.mailSender = mailSender;
		this.messageSource = messageSource;
		this.userRepository = userRepository;
		this.localeResolutionService = localeResolutionService;
	}

	private String msg(Locale locale, String key, Object... args) {
		return messageSource.getMessage(key, args, key, locale);
	}

	private Locale resolveRecipientLocale(String recipientEmail, String fallbackLanguageTag) {
		LanguagePreference preferred = userRepository.findPreferredLanguageByUsername(recipientEmail).orElse(null);
		return localeResolutionService.resolveForRecipient(preferred, fallbackLanguageTag);
	}

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

	/** Verificación de cuenta: valida consumo de token VERIFY y lo borra */
	public boolean consumeVerificationToken(String token) {
		return verificationTokenRepository.findByTokenAndPurpose(token, TokenPurpose.VERIFY)
				.filter(t -> !t.isExpired())
				.map(t -> {
					verificationTokenRepository.delete(t);
					return true;
				}).orElse(false);
	}

	/** Reset de password: devuelve email si token RESET es válido y lo borra */
	public String consumeResetPasswordToken(String token) {
		return verificationTokenRepository.findByTokenAndPurpose(token, TokenPurpose.RESET)
				.filter(t -> !t.isExpired())
				.map(t -> {
					verificationTokenRepository.delete(t);
					return t.getEmail();
				}).orElse(null);
	}

	public void sendVerificationEmail(String email, String token, String fallbackLanguageTag) {
		LanguagePreference languagePreference = LanguagePreference.fromAcceptLanguageHeader(fallbackLanguageTag);
		Locale locale = Locale.forLanguageTag(languagePreference.name().toLowerCase(Locale.ROOT));
		logger.info("Verification email locale resolved as {} for {} (fallback={})", locale, email,
				fallbackLanguageTag);
		sendVerificationEmail(email, token, locale);
	}

	public void sendVerificationEmail(String email, String token, Locale locale) {
		String subject = msg(locale, "email.verify.account");
		String verificationLink = verificationUrl + "?token=" + token;
		String text = msg(locale, "email.verification.text", verificationLink);
		String html = buildActionEmailHtml(
				locale,
				subject,
				msg(locale, "email.verification.intro"),
				msg(locale, "email.verification.button"),
				verificationLink);
		sendEmail(email, subject, text, html, locale);
	}

	public void sendResetPasswordEmail(String email, String token, String fallbackLanguageTag) {
		Locale locale = resolveRecipientLocale(email, fallbackLanguageTag);
		String subject = msg(locale, "email.reset.password");
		String resetLink = resetPasswordUrl + "?token=" + token;
		String text = msg(locale, "email.reset.password.text", resetLink);
		String html = buildActionEmailHtml(
				locale,
				subject,
				msg(locale, "email.reset.password.intro"),
				msg(locale, "email.reset.password.button"),
				resetLink);
		sendEmail(email, subject, text, html, locale);
	}

	public void sendRoleActivatedEmail(
			String email,
			String rolNombre,
			String personaNombre,
			String personaApellido,
			String empresaNombre,
			String fallbackLanguageTag) {
		Locale locale = resolveRecipientLocale(email, fallbackLanguageTag);
		String subject = msg(locale, "email.role.activated");

		String fullName = String.join(
				" ",
				personaNombre != null ? personaNombre.trim() : "",
				personaApellido != null ? personaApellido.trim() : "")
				.trim();

		String safeName = fullName.isBlank() ? msg(locale, "email.user.fallback") : fullName;
		String safeRol = (rolNombre == null || rolNombre.isBlank()) ? "N/A" : rolNombre.trim();
		String safeEmpresa = (empresaNombre == null || empresaNombre.isBlank()) ? "N/A" : empresaNombre.trim();

		String text = msg(locale, "email.role.activated.body", safeName, safeRol, safeEmpresa);
		sendEmail(email, subject, text, locale);
	}

	public void sendNewUserCredentialsEmail(
			String email,
			String personaNombre,
			String personaApellido,
			String empresaNombre,
			String rolNombre,
			String tempPassword,
			String fallbackLanguageTag) {
		Locale locale = resolveRecipientLocale(email, fallbackLanguageTag);
		String subject = msg(locale, "email.new.user.credentials.subject");

		String fullName = String.join(
				" ",
				personaNombre != null ? personaNombre.trim() : "",
				personaApellido != null ? personaApellido.trim() : "")
				.trim();

		String safeName = fullName.isBlank() ? msg(locale, "email.user.fallback") : fullName;
		String safeRol = (rolNombre == null || rolNombre.isBlank()) ? "N/A" : rolNombre.trim();
		String safeEmpresa = (empresaNombre == null || empresaNombre.isBlank()) ? "N/A" : empresaNombre.trim();

		String text = msg(locale, "email.new.user.credentials.body", safeName, safeEmpresa, safeRol, email,
				tempPassword);
		sendEmail(email, subject, text, locale);
	}

	private void sendEmail(String to, String subject, String text, Locale locale) {
		sendEmail(to, subject, text, null, locale);
	}

	private void sendEmail(String to, String subject, String text, String htmlText, Locale locale) {
		if (htmlText != null && !htmlText.isBlank()) {
			sendHtmlEmail(to, subject, text, htmlText, locale);
			return;
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		try {
			mailSender.send(message);
			logger.info(msg(locale, "email.sent"), to, subject);
		} catch (MailException e) {
			logger.error(msg(locale, "email.send.failed"), to, e.getMessage(), e);
		}
	}

	private void sendHtmlEmail(String to, String subject, String text, String htmlText, Locale locale) {
		try {
			var message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, htmlText);
			mailSender.send(message);
			logger.info(msg(locale, "email.sent"), to, subject);
		} catch (MailException | MessagingException e) {
			logger.error(msg(locale, "email.send.failed"), to, e.getMessage(), e);
		}
	}

	private String buildActionEmailHtml(Locale locale, String title, String intro, String buttonText,
			String actionUrl) {
		String safeTitle = escapeHtml(title);
		String safeIntro = escapeHtml(intro);
		String safeButtonText = escapeHtml(buttonText);
		String safeActionUrl = escapeHtml(actionUrl);
		String safeFallbackText = escapeHtml(msg(locale, "email.action.fallback"));
		String safeIgnoreText = escapeHtml(msg(locale, "email.action.ignore"));
		String safeHtmlLang = escapeHtml(locale != null ? locale.getLanguage() : "en");

		return """
				<!DOCTYPE html>
				<html lang="%s">
				<head>
				  <meta charset="UTF-8">
				  <meta name="viewport" content="width=device-width, initial-scale=1.0">
				  <title>%s</title>
				</head>
				<body style="margin:0;padding:24px;background-color:#f5f7fb;font-family:Arial,sans-serif;color:#1f2937;">
				  <div style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:12px;padding:24px;">
				    <h2 style="margin:0 0 12px 0;font-size:22px;line-height:1.3;">%s</h2>
				    <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;">%s</p>
				    <a href="%s" style="display:inline-block;background:#16a34a;color:#ffffff;text-decoration:none;font-weight:700;padding:12px 18px;border-radius:8px;">
				      %s
				    </a>
				    <p style="margin:20px 0 8px 0;font-size:13px;line-height:1.6;color:#4b5563;">%s</p>
				    <p style="margin:0;font-size:13px;line-height:1.6;word-break:break-all;">
				      <a href="%s" style="color:#2563eb;text-decoration:underline;">%s</a>
				    </p>
				    <p style="margin:20px 0 0 0;font-size:12px;line-height:1.5;color:#6b7280;">%s</p>
				  </div>
				</body>
				</html>
				"""
				.formatted(
						safeHtmlLang,
						safeTitle,
						safeTitle,
						safeIntro,
						safeActionUrl,
						safeButtonText,
						safeFallbackText,
						safeActionUrl,
						safeActionUrl,
						safeIgnoreText);
	}

	private String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
