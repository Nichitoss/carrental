package com.example.carrental.controller;

import com.example.carrental.dto.auth.AuthResponse;
import com.example.carrental.dto.auth.LoginRequest;
import com.example.carrental.dto.auth.RegisterRequest;
import com.example.carrental.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse auth = authService.register(request);
        setCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        setCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken,
                                                HttpServletResponse response) {
        AuthResponse auth = authService.refresh(refreshToken);
        setCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "accessToken", required = false) String accessToken,
                                       @CookieValue(value = "refreshToken", required = false) String refreshToken,
                                       HttpServletResponse response) {
        clearCookie(response, "accessToken");
        clearCookie(response, "refreshToken");
        return ResponseEntity.ok().build();
    }

    private void setCookies(HttpServletResponse response, AuthResponse auth) {
        Cookie access = new Cookie("accessToken", auth.accessToken());
        access.setHttpOnly(true);
        access.setPath("/");
        response.addCookie(access);
        Cookie refresh = new Cookie("refreshToken", auth.refreshToken());
        refresh.setHttpOnly(true);
        refresh.setPath("/api/auth/refresh");
        response.addCookie(refresh);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

