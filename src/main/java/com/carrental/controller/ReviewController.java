package com.carrental.controller;

import com.carrental.dto.review.ReviewRequest;
import com.carrental.model.User;
import com.carrental.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.list(user));
    }

    @PostMapping("/rental/{rentalId}")
    public ResponseEntity<?> create(@PathVariable Long rentalId,
                                    @Valid @RequestBody ReviewRequest req,
                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.create(rentalId, req, user));
    }
}

