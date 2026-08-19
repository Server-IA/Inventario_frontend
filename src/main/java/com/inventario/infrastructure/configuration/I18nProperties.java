package com.inventario.infrastructure.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.i18n")
public class I18nProperties {

    private String defaultLanguage = "es";

    private List<String> supportedLanguages = List.of("es", "en");
}
