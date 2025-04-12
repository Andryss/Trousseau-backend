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
import ru.andryss.trousseau.service.TimeService;

@RequiredArgsConstructor
public class JwtTokenUtil implements InitializingBean {

    private final JwtProperties properties;
    private final TimeService timeService;

    private Key signingKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] bytes = Decoders.BASE64.decode(properties.getSecret());
        signingKey = Keys.hmacShaKeyFor(bytes);
        properties.setSecret(null);
    }

    public String generateAccessToken(UserData userData) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + properties.getTokenExpirationMillis());
        return Jwts.builder()
                .setSubject(userData.getId())
                .setClaims(Map.of("data", userData))
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

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

    public UserData extractUserData(String token) {
        Claims body = getTokenClaims(token);
        return body.get("data", UserData.class);
    }

    private Claims getTokenClaims(String token) throws JwtException {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
