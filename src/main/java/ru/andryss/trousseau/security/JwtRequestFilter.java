package ru.andryss.trousseau.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andryss.trousseau.config.SecurityConfig;
import ru.andryss.trousseau.generated.model.ErrorObject;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String ERROR_MESSAGE = "Пользователь не авторизован";

    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapperWrapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> token = getTokenFromRequest(request);
        if (token.isPresent() && jwtTokenUtil.isTokenValid(token.get())) {
            try {
                UserData userData = jwtTokenUtil.extractUserData(token.get());
                UsernamePasswordAuthenticationToken authentication =
                        authenticated(userData, null, userData.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("Catch exception when parsing user token", e);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType(SecurityConfig.RESPONSE_CONTENT_TYPE);
                response.setCharacterEncoding(SecurityConfig.RESPONSE_CHARACTER_ENCODING);
                ErrorObject error = new ErrorObject()
                        .code(401).message("user.unauthorized.error").humanMessage(ERROR_MESSAGE);
                PrintWriter writer = response.getWriter();
                writer.write(objectMapper.writeValueAsString(error));
                writer.flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer == null) return Optional.empty();
        return bearer.startsWith("Bearer ") ? Optional.of(bearer.substring(7)) : Optional.empty();
    }
}
