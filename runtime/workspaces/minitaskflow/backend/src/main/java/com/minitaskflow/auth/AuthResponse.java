package com.minitaskflow.auth;

import com.minitaskflow.users.UserResponse;

public record AuthResponse(String token, UserResponse user) {
}
