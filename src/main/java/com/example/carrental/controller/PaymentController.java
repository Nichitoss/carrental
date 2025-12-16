package com.example.carrental.controller;

import com.example.carrental.dto.PaymentDto;
import com.example.carrental.model.PaymentStatus;
import com.example.carrental.security.AppUserDetails;
import com.example.carrental.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/me")
    public List<PaymentDto> myPayments(Authentication authentication) {
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        return paymentService.findByUser(user.getUser().getId());
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public List<PaymentDto> all() {
        return paymentService.findAll();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping
    public PaymentDto create(@RequestParam Long rentalId, @RequestBody PaymentDto dto) {
        return paymentService.create(rentalId, dto);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PatchMapping("/{id}/status")
    public PaymentDto updateStatus(@PathVariable Long id, @RequestParam PaymentStatus status) {
        return paymentService.updateStatus(id, status);
    }
}

