package com.minitaskflow.users;

public record UserResponse(Long id, String email, AuthProvider authProvider, UserRole role) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getAuthProvider(), user.getRole());
    }
}
