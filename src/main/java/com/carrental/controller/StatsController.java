package com.carrental.controller;

import com.carrental.repository.PaymentRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class StatsController {

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public StatsController(RentalRepository rentalRepository, PaymentRepository paymentRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> overview() {
        var totalRentals = rentalRepository.count();
        var totalPayments = paymentRepository.findAll().stream()
                .map(p -> p.getAmount() == null ? BigDecimal.ZERO : p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var users = userRepository.count();
        return ResponseEntity.ok(Map.of(
                "totalRentals", totalRentals,
                "totalPayments", totalPayments,
                "users", users
        ));
    }
}

