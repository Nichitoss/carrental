package com.carrental.controller;

import com.carrental.dto.rental.RentalRequest;
import com.carrental.dto.rental.RentalStatusUpdateRequest;
import com.carrental.model.User;
import com.carrental.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rentalService.listForUser(user));
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "status", required = false) String status,
                                    @RequestParam(name = "userId", required = false) Long userId,
                                    Authentication auth) {
        return ResponseEntity.ok(rentalService.search(status, userId, auth));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RentalRequest req,
                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rentalService.create(req, user));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @Valid @RequestBody RentalStatusUpdateRequest req,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rentalService.updateStatus(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        rentalService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rentalService.get(id, user));
    }
}

