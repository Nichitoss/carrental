package com.carrental.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ReviewDto {
    private Long id;
    private Long rentalId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}

