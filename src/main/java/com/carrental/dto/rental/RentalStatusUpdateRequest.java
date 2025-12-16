package com.carrental.dto.rental;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalStatusUpdateRequest {
    @NotBlank
    private String status;
}

