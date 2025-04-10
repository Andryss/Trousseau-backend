package ru.andryss.trousseau.security;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import ru.andryss.trousseau.config.JwtProperties;

@RequiredArgsConstructor
public class JwtTokenUtil implements InitializingBean {

    private final JwtProperties properties;

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
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            return currentTime <= expirationTime;
        } catch (Exception e) {
            return false;
        }
    }

    public UserData extractUserData(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
        Claims body = jwtParser.parseClaimsJws(token).getBody();
        return body.get("data", UserData.class);
    }
}
