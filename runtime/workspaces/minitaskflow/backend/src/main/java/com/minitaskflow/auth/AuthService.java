package com.minitaskflow.auth;

import com.minitaskflow.users.AppUser;
import com.minitaskflow.users.AuthProvider;
import com.minitaskflow.users.UserRepository;
import com.minitaskflow.users.UserResponse;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleIdentityService googleIdentityService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            GoogleIdentityService googleIdentityService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleIdentityService = googleIdentityService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
        }
        AppUser user = userRepository.save(new AppUser(email, passwordEncoder.encode(request.password())));
        return responseFor(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        String email = normalizeEmail(request.email());
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        if (user.getAuthProvider() != AuthProvider.LOCAL || user.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        return responseFor(user);
    }

    @Transactional
    public AuthResponse google(GoogleAuthRequest request) {
        GoogleIdentityClaims claims = googleIdentityService.verify(request);
        AppUser user = userRepository.findByGoogleSub(claims.googleSub())
                .orElseGet(() -> createGoogleUser(claims));
        return responseFor(user);
    }

    private AppUser createGoogleUser(GoogleIdentityClaims claims) {
        String email = normalizeEmail(claims.email());
        userRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account already exists. Use password login.");
        });
        return userRepository.save(AppUser.google(email, claims.googleSub(), claims.emailVerified()));
    }

    private AuthResponse responseFor(AppUser user) {
        return new AuthResponse(jwtService.issueToken(user), UserResponse.from(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
