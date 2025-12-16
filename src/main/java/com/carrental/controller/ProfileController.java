package com.carrental.controller;

import com.carrental.dto.user.ProfileUpdateRequest;
import com.carrental.model.User;
import com.carrental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserService.toDto(user));
    }

    @PutMapping
    public ResponseEntity<?> update(@AuthenticationPrincipal User user,
                                    @Valid @RequestBody ProfileUpdateRequest req) {
        return ResponseEntity.ok(userService.updateProfile(user, req.getFirstName(), req.getLastName(), req.getEmail()));
    }
}

