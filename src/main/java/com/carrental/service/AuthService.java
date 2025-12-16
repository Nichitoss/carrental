package com.carrental.service;

import com.carrental.dto.auth.AuthResponse;
import com.carrental.dto.auth.LoginRequest;
import com.carrental.dto.auth.RefreshRequest;
import com.carrental.dto.auth.RegisterRequest;
import com.carrental.exception.BadRequestException;
import com.carrental.model.RefreshToken;
import com.carrental.model.User;
import com.carrental.repository.RefreshTokenRepository;
import com.carrental.repository.RoleRepository;
import com.carrental.repository.UserRepository;
import com.carrental.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already used");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already used");
        }
        var role = roleRepository.findByRoleName("CLIENT")
                .orElseThrow(() -> new BadRequestException("Default role missing"));
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(role)
                .active(true)
                .build();
        userRepository.save(user);
        return buildTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BadRequestException("User is blocked");
        }
        return buildTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        if (token.getRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Expired refresh token");
        }
        User user = token.getUser();
        return buildTokens(user);
    }

    @Transactional
    public void logoutCurrent() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            userRepository.findByUsername(auth.getName()).ifPresent(user -> {
                refreshTokenRepository.deleteByUser(user);
            });
        }
    }

//    @Transactional
//    protected AuthResponse buildTokens(User user) {
//        refreshTokenRepository.deleteByUser(user);
//        String access = jwtService.generateAccessToken(user);
//        String refresh = jwtService.generateRefreshToken(user);
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(user);
//        refreshToken.setToken(refresh);
//        refreshToken.setExpiresAt(Instant.now().plus(15, ChronoUnit.DAYS));
//        refreshTokenRepository.save(refreshToken);
//        return AuthResponse.builder()
//                .accessToken(access)
//                .refreshToken(refresh)
//                .user(UserService.toDto(user))
//                .build();
//    }
    @Transactional
    public AuthResponse buildTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refresh);
        refreshToken.setExpiresAt(Instant.now().plus(15, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshToken);
        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .user(UserService.toDto(user))
                .build();

    }

}

