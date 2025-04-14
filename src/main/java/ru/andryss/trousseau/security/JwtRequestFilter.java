package ru.andryss.trousseau.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andryss.trousseau.config.SecurityConfig;
import ru.andryss.trousseau.generated.model.ErrorObject;
import ru.andryss.trousseau.model.SessionEntity;
import ru.andryss.trousseau.service.ObjectMapperWrapper;
import ru.andryss.trousseau.service.SessionService;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String ERROR_MESSAGE = "Пользователь не авторизован";

    private final JwtTokenUtil jwtTokenUtil;
    private final SessionService sessionService;
    private final ObjectMapperWrapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> tokenOptional = getTokenFromRequest(request);
        if (tokenOptional.isPresent() && jwtTokenUtil.isTokenValid(tokenOptional.get())) {
            String token = tokenOptional.get();
            Optional<SessionEntity> sessionOptional = sessionService.getById(token);
            if (sessionOptional.isEmpty()) {
                error(response, new ErrorObject()
                        .code(401).message("user.unauthorized.session.absent").humanMessage(ERROR_MESSAGE));
                return;
            }
            SessionEntity session = sessionOptional.get();
            try {
                UserData userData = jwtTokenUtil.extractUserData(token);
                if (!session.getUserId().equals(userData.getId())) {
                    error(response, new ErrorObject()
                            .code(401).message("user.unauthorized.session.different").humanMessage(ERROR_MESSAGE));
                    return;
                }
                List<SimpleGrantedAuthority> authorities = userData.getPrivileges().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                UsernamePasswordAuthenticationToken authentication =
                        authenticated(userData, token, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("Catch exception when parsing user token", e);
                error(response, new ErrorObject()
                        .code(401).message("user.unauthorized.error").humanMessage(ERROR_MESSAGE));
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

    private void error(@NotNull HttpServletResponse response, ErrorObject error) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(SecurityConfig.RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(SecurityConfig.RESPONSE_CHARACTER_ENCODING);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(error));
        writer.flush();
    }
}
