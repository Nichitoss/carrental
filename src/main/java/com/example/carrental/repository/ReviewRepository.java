package com.example.carrental.repository;

import com.example.carrental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRentalUserId(Long userId);
}

