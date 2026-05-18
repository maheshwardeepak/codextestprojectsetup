package com.minitaskflow.auth;

public record GoogleIdentityClaims(String googleSub, String email, boolean emailVerified) {
}
