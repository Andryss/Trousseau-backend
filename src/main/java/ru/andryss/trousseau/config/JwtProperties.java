package ru.andryss.trousseau.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties("jwt.token")
public class JwtProperties {
    @NotBlank
    private String secret;
    @NotNull
    @Positive
    private Long tokenExpirationMillis;
}
