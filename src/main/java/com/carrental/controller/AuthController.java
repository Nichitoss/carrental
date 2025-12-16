package com.carrental.controller;

import com.carrental.dto.auth.AuthResponse;
import com.carrental.dto.auth.LoginRequest;
import com.carrental.dto.auth.RefreshRequest;
import com.carrental.dto.auth.RegisterRequest;
import com.carrental.dto.common.ApiResponse;
import com.carrental.service.AuthService;
import com.carrental.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.carrental.model.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                 HttpServletResponse response) {
        AuthResponse auth = authService.register(request);
        setCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        setCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request,
                                                HttpServletResponse response) {
        AuthResponse auth = authService.refresh(request);
        setCookies(response, auth);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        authService.logoutCurrent();
        clearCookies(response);
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Logged out").build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(UserService.toDto(user));
    }

    // Временный эндпоинт для генерации хэша пароля (удалить после использования)
    @GetMapping("/generate-hash")
    public ResponseEntity<?> generateHash(@RequestParam String password,
                                         org.springframework.security.crypto.password.PasswordEncoder encoder) {
        return ResponseEntity.ok(java.util.Map.of("password", password, "hash", encoder.encode(password)));
    }

    private void setCookies(HttpServletResponse response, AuthResponse auth) {
        ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", auth.getAccessToken())
                .httpOnly(true).path("/").maxAge(60 * 30).build();
        ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", auth.getRefreshToken())
                .httpOnly(true).path("/").maxAge(60L * 60 * 24 * 15).build();
        response.addHeader("Set-Cookie", access.toString());
        response.addHeader("Set-Cookie", refresh.toString());
    }

    private void clearCookies(HttpServletResponse response) {
        ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true).path("/").maxAge(0).build();
        ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true).path("/").maxAge(0).build();
        response.addHeader("Set-Cookie", access.toString());
        response.addHeader("Set-Cookie", refresh.toString());
    }
}

