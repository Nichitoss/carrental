package com.example.carrental.controller;

import com.example.carrental.dto.RentalDto;
import com.example.carrental.model.RentalStatus;
import com.example.carrental.model.RoleName;
import com.example.carrental.security.AppUserDetails;
import com.example.carrental.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public List<RentalDto> all() {
        return rentalService.findAll();
    }

    @GetMapping("/me")
    public List<RentalDto> myRentals(Authentication authentication) {
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        return rentalService.findByUser(user.getUser().getId());
    }

    @PostMapping
    public RentalDto create(Authentication authentication, @RequestParam Long vehicleId, @RequestBody RentalDto dto) {
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        return rentalService.createForUser(user.getUser().getId(), vehicleId, dto, null);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/for-user/{userId}")
    public RentalDto createForUser(@PathVariable Long userId, @RequestParam Long vehicleId,
                                   Authentication authentication, @RequestBody RentalDto dto) {
        AppUserDetails manager = (AppUserDetails) authentication.getPrincipal();
        return rentalService.createForUser(userId, vehicleId, dto, manager.getUser().getId());
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PatchMapping("/{id}/status")
    public RentalDto updateStatus(@PathVariable Long id, @RequestParam RentalStatus status,
                                  Authentication authentication) {
        AppUserDetails manager = (AppUserDetails) authentication.getPrincipal();
        return rentalService.updateStatus(id, status, manager.getUser().getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

