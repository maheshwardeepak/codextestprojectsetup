package com.minitaskflow.shared;

import java.time.Instant;
import java.util.Map;

public record ApiError(String message, Map<String, String> fieldErrors, Instant timestamp) {
    public static ApiError of(String message) {
        return new ApiError(message, Map.of(), Instant.now());
    }

    public static ApiError of(String message, Map<String, String> fieldErrors) {
        return new ApiError(message, fieldErrors, Instant.now());
    }
}
