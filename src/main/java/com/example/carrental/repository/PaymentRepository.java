package com.example.carrental.repository;

import com.example.carrental.entity.Payment;
import com.example.carrental.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRentalUserId(Long userId);
    List<Payment> findByStatus(PaymentStatus status);
}

