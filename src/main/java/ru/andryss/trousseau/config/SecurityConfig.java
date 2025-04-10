package ru.andryss.trousseau.config;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.andryss.trousseau.generated.model.ErrorObject;
import ru.andryss.trousseau.security.JwtRequestFilter;
import ru.andryss.trousseau.security.JwtTokenUtil;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

@Slf4j
@Configuration
@Profile("!functionalTest")
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String RESPONSE_CONTENT_TYPE = "application/json; charset=UTF-8";
    public static final String RESPONSE_CHARACTER_ENCODING = "UTF-8";

    private final JwtProperties properties;
    private final ObjectMapperWrapper objectMapper;

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil(properties);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtTokenUtil(), objectMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/ping").permitAll()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> c
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.info("Got auth exception on {} {}: {}", request.getMethod(), request.getRequestURI(),
                    authException.getMessage());
            writeErrorResponse(response, new ErrorObject()
                    .code(401).message("user.unauthorized").humanMessage("Пользователь не авторизован"));
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.info("Got access denied exception on {} {}: {}", request.getMethod(), request.getRequestURI(),
                    accessDeniedException.getMessage());
            writeErrorResponse(response, new ErrorObject()
                    .code(403).message("user.forbidden").humanMessage("Доступ запрещен"));
        };
    }

    private void writeErrorResponse(HttpServletResponse response, Object error) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(RESPONSE_CHARACTER_ENCODING);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(error));
        writer.flush();
    }

}
