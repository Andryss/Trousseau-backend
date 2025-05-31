package ru.andryss.trousseau.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andryss.trousseau.security.JwtTokenUtil;
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
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return Mockito.mock(JwtTokenUtil.class);
    }

    @Bean
    public RuntimeMessagingService runtimeMessagingService() {
        return Mockito.mock(RuntimeMessagingService.class);
    }

}
