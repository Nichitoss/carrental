package com.carrental.controller;

import com.carrental.dto.common.ApiResponse;
import com.carrental.dto.user.UpdateProfileRequest;
import com.carrental.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserService::toDto).toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/block")
    public ResponseEntity<ApiResponse> block(@PathVariable Long id) {
        userService.setActive(id, false);
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("User blocked").build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/unblock")
    public ResponseEntity<ApiResponse> unblock(@PathVariable Long id) {
        userService.setActive(id, true);
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("User unblocked").build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/role/{role}")
    public ResponseEntity<ApiResponse> changeRole(@PathVariable Long id, @PathVariable String role) {
        userService.changeRole(id, role);
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Role updated").build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal com.carrental.model.User user) {
        return ResponseEntity.ok(UserService.toDto(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@AuthenticationPrincipal com.carrental.model.User user,
                                      @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(UserService.toDto(userService.updateProfile(user, req)));
    }
}

