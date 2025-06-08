package com.coagronet.infrastructure.configuration;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.web.PagedResourcesAssembler;

@Configuration
@PropertySource("classpath:queries.properties")
@ConfigurationProperties(prefix = "myapp")
public class AppConfig {

    private Map<String, String> queries;

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }

}
