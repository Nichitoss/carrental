package com.carrental.repository;

import com.carrental.model.Review;
import com.carrental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRental(Rental rental);
    List<Review> findByRental_User_Id(Long userId);
}

