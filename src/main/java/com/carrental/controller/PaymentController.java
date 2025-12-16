package com.carrental.controller;

import com.carrental.dto.payment.PaymentRequest;
import com.carrental.model.User;
import com.carrental.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.list(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.get(id, user));
    }

    @PostMapping("/rental/{rentalId}")
    public ResponseEntity<?> create(@PathVariable Long rentalId,
                                    @Valid @RequestBody PaymentRequest req,
                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.create(rentalId, req, user));
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam("status") String status,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.updateStatus(id, status, user));
    }
}

