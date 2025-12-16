package com.carrental.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class PaymentDto {
    private Long id;
    private Long rentalId;
    private BigDecimal amount;
    private String status;
    private String method;
    private Instant paidAt;
}

