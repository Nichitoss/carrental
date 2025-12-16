package com.carrental.dto.vehicle;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class VehicleDto {
    private Long id;
    private String licensePlate;
    private String manufacturer;
    private String model;
    private Integer year;
    private BigDecimal dailyPrice;
    private String status;
    private String color;
    private BigDecimal mileage;
}

