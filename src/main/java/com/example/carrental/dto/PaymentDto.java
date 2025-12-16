package com.example.carrental.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDto(
        Long id,
        Long rentalId,
        BigDecimal amount,
        String status,
        String method,
        Instant paidAt
) {}

