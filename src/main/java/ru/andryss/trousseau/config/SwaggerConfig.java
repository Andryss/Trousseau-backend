package ru.andryss.trousseau.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!functionalTest")
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi openApi() {
        return GroupedOpenApi.builder()
                .group("trousseau-backend")
                .pathsToMatch("/**")
                .pathsToExclude("/ping")
                .build();
    }
}
