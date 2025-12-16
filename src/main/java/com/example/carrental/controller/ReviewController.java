package com.example.carrental.controller;

import com.example.carrental.dto.ReviewDto;
import com.example.carrental.security.AppUserDetails;
import com.example.carrental.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/me")
    public List<ReviewDto> myReviews(Authentication authentication) {
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        return reviewService.findByUser(user.getUser().getId());
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public List<ReviewDto> all() {
        return reviewService.findAll();
    }

    @PostMapping
    public ReviewDto create(@RequestParam Long rentalId, @RequestBody ReviewDto dto) {
        return reviewService.create(rentalId, dto);
    }
}

