package ru.andryss.trousseau.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "messaging")
public class MessagingProperties {
    private boolean enabled = false;
    private String filepath;
}
