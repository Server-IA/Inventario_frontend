package com.coagronet.infrastructure.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.coagronet.infrastructure.configuration.I18nProperties;
import com.coagronet.user.LanguagePreference;
import com.coagronet.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocaleResolutionService {

    private final I18nProperties i18nProperties;
    private final UserRepository userRepository;

    /**
     * HTTP: Accept-Language > perfil usuario > default sistema.
     */
    public Locale resolveForHttpRequest(String acceptLanguageHeader, String username) {
        Locale fromHeader = resolveFromAcceptLanguageHeader(acceptLanguageHeader);
        if (fromHeader != null) {
            return fromHeader;
        }

        Locale fromProfile = resolveFromUserProfile(username);
        if (fromProfile != null) {
            return fromProfile;
        }

        return defaultLocale();
    }

    /**
     * Notificaciones/correos: perfil destinatario > fallback informado > default
     * sistema.
     */
    public Locale resolveForRecipient(LanguagePreference recipientLanguage, String fallbackLanguageTag) {
        if (recipientLanguage != null) {
            return toLocale(recipientLanguage);
        }

        Locale fallback = resolveFromLanguageTag(fallbackLanguageTag);
        if (fallback == null) {
            fallback = resolveFromAcceptLanguageHeader(fallbackLanguageTag);
        }
        if (fallback != null) {
            return fallback;
        }

        return defaultLocale();
    }

    private Locale resolveFromUserProfile(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        Optional<LanguagePreference> preferredLanguage = userRepository.findPreferredLanguageByUsername(username);
        return preferredLanguage.map(this::toLocale).orElse(null);
    }

    private Locale resolveFromAcceptLanguageHeader(String header) {
        String normalizedHeader = sanitizeLanguageInput(header);
        if (normalizedHeader == null || normalizedHeader.isBlank()) {
            return null;
        }

        try {
            List<Locale.LanguageRange> ranges = Locale.LanguageRange.parse(normalizedHeader);
            Locale matched = Locale.lookup(ranges, supportedLocales());
            return matched != null ? matched : null;
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private Locale resolveFromLanguageTag(String languageTag) {
        String normalizedTag = sanitizeLanguageInput(languageTag);
        if (normalizedTag == null || normalizedTag.isBlank()) {
            return null;
        }
        Locale candidate = Locale.forLanguageTag(normalizedTag);
        return isSupported(candidate) ? candidate : null;
    }

    private String sanitizeLanguageInput(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replace("\"", "");
    }

    private Locale toLocale(LanguagePreference preference) {
        return Locale.forLanguageTag(preference.name().toLowerCase(Locale.ROOT));
    }

    private Locale defaultLocale() {
        return Locale.forLanguageTag(i18nProperties.getDefaultLanguage());
    }

    private List<Locale> supportedLocales() {
        return i18nProperties.getSupportedLanguages()
                .stream()
                .map(Locale::forLanguageTag)
                .toList();
    }

    private boolean isSupported(Locale locale) {
        return supportedLocales().stream()
                .anyMatch(supported -> supported.getLanguage().equalsIgnoreCase(locale.getLanguage()));
    }
}
