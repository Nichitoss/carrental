package com.carrental.dto.rental;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RentalDto {
    private Long id;
    private Long userId;
    private String username;
    private Long vehicleId;
    private String vehicleModel;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private Long managerId;
}

