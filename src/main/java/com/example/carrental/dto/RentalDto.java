package com.example.carrental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalDto(
        Long id,
        Long userId,
        Long vehicleId,
        Long managerId,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice
) {}

