package com.example.carrental.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName
) {}

