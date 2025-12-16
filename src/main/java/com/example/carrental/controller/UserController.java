package com.example.carrental.controller;

import com.example.carrental.dto.UserDto;
import com.example.carrental.model.RoleName;
import com.example.carrental.security.AppUserDetails;
import com.example.carrental.service.AuthService;
import com.example.carrental.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public List<UserDto> all() {
        return userService.findAll();
    }

    @GetMapping("/me")
    public UserDto me(Authentication authentication) {
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        return userService.findById(user.getUser().getId());
    }

    @PutMapping("/me")
    public UserDto updateMe(Authentication authentication, @RequestBody UserDto dto) {
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        return userService.updateProfile(user.getUser().getId(), dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public UserDto changeRole(@PathVariable Long id, @RequestParam RoleName role) {
        return userService.changeRole(id, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/active")
    public UserDto toggle(@PathVariable Long id, @RequestParam boolean active) {
        return userService.toggleActive(id, active);
    }
}

