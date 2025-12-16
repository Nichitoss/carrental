package com.carrental.dto.vehicle;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VehicleRequest {
    @NotBlank
    private String licensePlate;
    @NotBlank
    private String manufacturer;
    @NotBlank
    private String model;
    @NotNull
    @Min(1900)
    private Integer year;
    @NotNull
    @Min(1)
    private BigDecimal dailyPrice;
    private String status;
    private String color;
    private BigDecimal mileage;
}

