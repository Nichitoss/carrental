package com.example.carrental.dto;

import java.math.BigDecimal;

public record VehicleDto(
        Long id,
        String licensePlate,
        String manufacturer,
        String model,
        Integer year,
        BigDecimal dailyPrice,
        String status,
        String color,
        BigDecimal mileage
) {}

