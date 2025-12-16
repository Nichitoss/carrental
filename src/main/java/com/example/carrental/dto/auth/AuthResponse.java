package com.example.carrental.dto.auth;

public record AuthResponse(
        String username,
        String role,
        String accessToken,
        String refreshToken
) {}

