package com.example.carrental.service;

import com.example.carrental.dto.PaymentDto;
import com.example.carrental.entity.Payment;
import com.example.carrental.entity.Rental;
import com.example.carrental.model.PaymentStatus;
import com.example.carrental.repository.PaymentRepository;
import com.example.carrental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    public PaymentService(PaymentRepository paymentRepository, RentalRepository rentalRepository) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<PaymentDto> findByUser(Long userId) {
        return paymentRepository.findByRentalUserId(userId).stream().map(this::toDto).toList();
    }

    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public PaymentDto create(Long rentalId, PaymentDto dto) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmount(dto.amount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setMethod(dto.method());
        return toDto(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentDto updateStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setStatus(status);
        if (status == PaymentStatus.PAID) {
            payment.setPaidAt(Instant.now());
        }
        return toDto(paymentRepository.save(payment));
    }

    private PaymentDto toDto(Payment p) {
        return new PaymentDto(
                p.getId(),
                p.getRental().getId(),
                p.getAmount(),
                p.getStatus().name(),
                p.getMethod(),
                p.getPaidAt()
        );
    }
}

