package com.minitaskflow.auth;

import com.minitaskflow.users.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final String secret;
    private final String issuer;
    private final Duration tokenLifetime;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration}") Duration tokenLifetime
    ) {
        this.secret = secret;
        this.issuer = issuer;
        this.tokenLifetime = tokenLifetime;
    }

    public String issueToken(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("token_type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(tokenLifetime)))
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!"access".equals(claims.get("token_type", String.class))) {
            throw new JwtException("Unexpected token type.");
        }
        return Long.valueOf(claims.getSubject());
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
