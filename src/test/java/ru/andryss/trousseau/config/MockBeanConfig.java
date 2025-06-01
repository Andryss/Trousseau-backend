package ru.andryss.trousseau.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.andryss.trousseau.service.RuntimeMessagingService;
import ru.andryss.trousseau.service.S3Service;

@Configuration
@Profile("functionalTest")
public class MockBeanConfig {

    @Bean
    public S3Service s3Service() {
        return Mockito.mock(S3Service.class);
    }

    @Bean
    public RuntimeMessagingService runtimeMessagingService() {
        return Mockito.mock(RuntimeMessagingService.class);
    }

}
