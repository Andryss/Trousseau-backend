package ru.andryss.trousseau.config;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
import ru.andryss.trousseau.service.SessionService;
import ru.andryss.trousseau.service.TimeService;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String RESPONSE_CONTENT_TYPE = "application/json; charset=UTF-8";
    public static final String RESPONSE_CHARACTER_ENCODING = "UTF-8";

    private final JwtProperties properties;
    private final SessionService sessionService;
    private final TimeService timeService;
    private final ObjectMapperWrapper objectMapper;

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil(properties, timeService, objectMapper);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtTokenUtil(), sessionService, objectMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs*/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> c
                        // ping
                        .requestMatchers(GET, "/ping").permitAll()
                        // auth
                        .requestMatchers(POST, "/auth/signup").permitAll()
                        .requestMatchers(POST, "/auth/signin").permitAll()
                        .requestMatchers(POST, "/auth/signout").permitAll()
                        .requestMatchers(GET, "/auth/profile").authenticated()
                        // media
                        .requestMatchers(POST, "/seller/media").hasAuthority("MEDIA_UPLOAD")
                        // seller
                        .requestMatchers(POST, "/seller/items").hasAuthority("ITEMS_CREATE")
                        .requestMatchers(GET, "/seller/items").hasAuthority("ITEMS_CREATED_VIEW")
                        .requestMatchers(GET, "/seller/items/*").hasAuthority("ITEMS_CREATED_VIEW")
                        .requestMatchers(PUT, "/seller/items/*").hasAuthority("ITEMS_CREATE")
                        .requestMatchers(PUT, "/seller/items/*/status").hasAuthority("ITEMS_CREATE")
                        .requestMatchers(GET, "/seller/items/*/booking").hasAuthority("ITEMS_CREATED_VIEW")
                        // category tree
                        .requestMatchers(GET, "/public/categories/tree").hasAuthority("CATEGORY_TREE_VIEW")
                        // public items
                        .requestMatchers(POST, "/public/items:search").hasAuthority("ITEMS_PUBLISHED_VIEW")
                        .requestMatchers(GET, "/public/items/*").hasAuthority("ITEMS_PUBLISHED_VIEW")
                        .requestMatchers(PUT, "/public/items/*/status").hasAuthority("ITEMS_PUBLISHED_STATUS_CHANGED")
                        .requestMatchers(GET, "/public/items/bookings").hasAuthority("ITEMS_PUBLISHED_VIEW")
                        // favourites
                        .requestMatchers(POST, "/public/items/*/favourite").hasAuthority("ITEMS_FAVOURITES")
                        .requestMatchers(GET, "/public/items/favourites").hasAuthority("ITEMS_FAVOURITES")
                        // subscriptions
                        .requestMatchers(POST, "/public/subscriptions").hasAuthority("SUBSCRIPTIONS_EDIT")
                        .requestMatchers(GET, "/public/subscriptions").hasAuthority("SUBSCRIPTIONS_VIEW")
                        .requestMatchers(PUT, "/public/subscriptions/*").hasAuthority("SUBSCRIPTIONS_EDIT")
                        .requestMatchers(DELETE, "/public/subscriptions/*").hasAuthority("SUBSCRIPTIONS_EDIT")
                        // notifications
                        .requestMatchers(GET, "/public/notifications").hasAuthority("NOTIFICATIONS_VIEW")
                        .requestMatchers(GET, "/public/notifications/unread/count").hasAuthority("NOTIFICATIONS_VIEW")
                        .requestMatchers(POST, "/public/notifications/*/read").hasAuthority("NOTIFICATIONS_VIEW")
                        .requestMatchers(POST, "/public/notifications/token").hasAuthority("NOTIFICATIONS_VIEW")
                        // other
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
