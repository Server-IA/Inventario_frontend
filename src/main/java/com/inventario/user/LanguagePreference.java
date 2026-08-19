package com.inventario.user;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum LanguagePreference {
    ES, EN;

    public static LanguagePreference from(String value) {
        if (value == null || value.isBlank()) {
            return ES;
        }
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(ES);
    }

    public static LanguagePreference fromAcceptLanguageHeader(String header) {
        String normalizedHeader = sanitizeLanguageInput(header);
        if (normalizedHeader == null || normalizedHeader.isBlank()) {
            return ES;
        }
        try {
            List<Locale.LanguageRange> ranges = Locale.LanguageRange.parse(normalizedHeader);
            Locale matched = Locale.lookup(ranges, List.of(Locale.forLanguageTag("es"), Locale.forLanguageTag("en")));
            return matched != null ? from(matched.getLanguage()) : ES;
        } catch (IllegalArgumentException ignored) {
            return ES;
        }
    }

    private static String sanitizeLanguageInput(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replace("\"", "");
    }
}
