package com.carrental.repository;

import com.carrental.model.Payment;
import com.carrental.model.PaymentStatus;
import com.carrental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRental(Rental rental);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByRental_User_Id(Long userId);
}

