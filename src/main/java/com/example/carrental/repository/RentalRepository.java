package com.example.carrental.repository;

import com.example.carrental.entity.Rental;
import com.example.carrental.model.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserId(Long userId);
    List<Rental> findByStatus(RentalStatus status);
}

