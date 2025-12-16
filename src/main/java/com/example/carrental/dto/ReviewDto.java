package com.example.carrental.dto;

public record ReviewDto(
        Long id,
        Long rentalId,
        Integer rating,
        String comment
) {}

