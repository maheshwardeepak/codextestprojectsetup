package com.minitaskflow.auth;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GoogleIdentityService {
    private static final List<String> ALLOWED_ISSUERS = List.of("https://accounts.google.com", "accounts.google.com");

    private final String clientId;
    private final String csrfToken;
    private final boolean testMode;
    private final JwtDecoder jwtDecoder;

    public GoogleIdentityService(
            @Value("${app.google.client-id}") String clientId,
            @Value("${app.google.csrf-token}") String csrfToken,
            @Value("${app.google.jwk-set-uri}") String jwkSetUri,
            @Value("${app.google.test-mode}") boolean testMode
    ) {
        this.clientId = clientId;
        this.csrfToken = csrfToken;
        this.testMode = testMode;
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    public GoogleIdentityClaims verify(GoogleAuthRequest request) {
        if (!isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Google login is not configured.");
        }
        validateCsrf(request.csrfToken());
        if (testMode) {
            return verifyTestCredential(request.credential());
        }
        return verifySignedGoogleToken(request.credential());
    }

    private boolean isConfigured() {
        return hasText(clientId) && hasText(csrfToken);
    }

    private void validateCsrf(String suppliedToken) {
        if (!csrfToken.equals(suppliedToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google login could not be verified.");
        }
    }

    private GoogleIdentityClaims verifySignedGoogleToken(String credential) {
        try {
            Jwt jwt = jwtDecoder.decode(credential);
            if (!jwt.getAudience().contains(clientId)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google login could not be verified.");
            }
            String issuer = jwt.getIssuer() == null ? "" : jwt.getIssuer().toString();
            if (!ALLOWED_ISSUERS.contains(issuer)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google login could not be verified.");
            }
            return claimsFrom(jwt.getSubject(), jwt.getClaimAsString("email"), jwt.getClaim("email_verified"));
        } catch (org.springframework.security.oauth2.jwt.JwtException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google login could not be verified.");
        }
    }

    private GoogleIdentityClaims verifyTestCredential(String credential) {
        String[] parts = credential.split("\\|", -1);
        if (parts.length != 4 || !"test-google-token".equals(parts[0])) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google login could not be verified.");
        }
        return claimsFrom(parts[1], parts[2], parts[3]);
    }

    private GoogleIdentityClaims claimsFrom(String googleSub, String email, Object emailVerifiedClaim) {
        if (!hasText(googleSub) || !hasText(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google login could not be verified.");
        }
        boolean emailVerified = Boolean.TRUE.equals(emailVerifiedClaim)
                || "true".equalsIgnoreCase(String.valueOf(emailVerifiedClaim));
        return new GoogleIdentityClaims(googleSub, email, emailVerified);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
