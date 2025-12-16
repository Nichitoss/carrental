package com.example.carrental.service;

import com.example.carrental.dto.ReviewDto;
import com.example.carrental.entity.Rental;
import com.example.carrental.entity.Review;
import com.example.carrental.repository.RentalRepository;
import com.example.carrental.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RentalRepository rentalRepository;

    public ReviewService(ReviewRepository reviewRepository, RentalRepository rentalRepository) {
        this.reviewRepository = reviewRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<ReviewDto> findByUser(Long userId) {
        return reviewRepository.findByRentalUserId(userId).stream().map(this::toDto).toList();
    }

    public List<ReviewDto> findAll() {
        return reviewRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public ReviewDto create(Long rentalId, ReviewDto dto) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        Review review = new Review();
        review.setRental(rental);
        review.setRating(dto.rating());
        review.setComment(dto.comment());
        return toDto(reviewRepository.save(review));
    }

    private ReviewDto toDto(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getRental().getId(),
                r.getRating(),
                r.getComment()
        );
    }
}

