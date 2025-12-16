package com.carrental.service;

import com.carrental.dto.review.ReviewDto;
import com.carrental.dto.review.ReviewRequest;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ForbiddenException;
import com.carrental.exception.NotFoundException;
import com.carrental.model.Rental;
import com.carrental.model.RentalStatus;
import com.carrental.model.Review;
import com.carrental.model.User;
import com.carrental.repository.RentalRepository;
import com.carrental.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RentalRepository rentalRepository;

    public ReviewService(ReviewRepository reviewRepository, RentalRepository rentalRepository) {
        this.reviewRepository = reviewRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<ReviewDto> list(User current) {
        if (hasRole(current, "CLIENT")) {
            return reviewRepository.findByRental_User_Id(current.getId()).stream()
                    .map(this::toDto).toList();
        }
        return reviewRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public ReviewDto create(Long rentalId, ReviewRequest req, User current) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (!rental.getUser().getId().equals(current.getId())) {
            throw new ForbiddenException("Can review only your rental");
        }
        if (rental.getStatus() != RentalStatus.COMPLETED) {
            throw new BadRequestException("Review only after completion");
        }
        Review review = new Review();
        review.setRental(rental);
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setCreatedAt(Instant.now());
        reviewRepository.save(review);
        return toDto(review);
    }

    private ReviewDto toDto(Review r) {
        return ReviewDto.builder()
                .id(r.getId())
                .rentalId(r.getRental().getId())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private boolean hasRole(User user, String role) {
        return user.getRole().getRoleName().equalsIgnoreCase(role);
    }
}

