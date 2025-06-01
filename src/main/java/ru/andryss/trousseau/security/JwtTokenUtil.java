package ru.andryss.trousseau.security;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import ru.andryss.trousseau.config.JwtProperties;
import ru.andryss.trousseau.service.ObjectMapperWrapper;
import ru.andryss.trousseau.service.TimeService;

@RequiredArgsConstructor
public class JwtTokenUtil implements InitializingBean {

    public static final String USER_DATA_KEY = "data";

    private final JwtProperties properties;
    private final TimeService timeService;
    private final ObjectMapperWrapper objectMapper;

    private Key signingKey;

    @Override
    public void afterPropertiesSet() {
        byte[] bytes = Decoders.BASE64.decode(properties.getSecret());
        signingKey = Keys.hmacShaKeyFor(bytes);
        properties.setSecret(null);
    }

    /**
     * Сгенерировать JWT-токен по информации о пользователе
     */
    public String generateAccessToken(UserData userData) {
        Date now = new Date(timeService.epochMillis());
        Date expired = new Date(now.getTime() + properties.getTokenExpirationMillis());
        return Jwts.builder()
                .setSubject(userData.getId())
                .setClaims(Map.of(USER_DATA_KEY, objectMapper.writeValueAsString(userData)))
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Провалидировать заданный токен (корректно сформирован + срок действия не истек)
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getTokenClaims(token);
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = timeService.epochMillis();
            return currentTime <= expirationTime;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Извлечь данные пользователя из токена
     */
    public UserData extractUserData(String token) {
        Claims body = getTokenClaims(token);
        String data = body.get(USER_DATA_KEY, String.class);
        return objectMapper.readValue(data, UserData.class);
    }

    private Claims getTokenClaims(String token) throws JwtException {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .setClock(timeService)
                .build();
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
