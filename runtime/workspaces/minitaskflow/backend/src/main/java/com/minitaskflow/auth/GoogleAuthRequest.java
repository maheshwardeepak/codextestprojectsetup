package com.minitaskflow.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        @NotBlank String credential,
        @NotBlank String csrfToken
) {
}
