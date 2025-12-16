package com.carrental.service;

import com.carrental.dto.payment.PaymentDto;
import com.carrental.dto.payment.PaymentRequest;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ForbiddenException;
import com.carrental.exception.NotFoundException;
import com.carrental.model.Payment;
import com.carrental.model.PaymentStatus;
import com.carrental.model.Rental;
import com.carrental.model.User;
import com.carrental.repository.PaymentRepository;
import com.carrental.repository.RentalRepository;
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

    @Transactional(readOnly = true)
    public List<PaymentDto> list(User current) {
        if (hasRole(current, "CLIENT")) {
            return paymentRepository.findByRental_User_Id(current.getId()).stream()
                    .map(this::toDto)
                    .toList();
        }
        return paymentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PaymentDto get(Long id, User current) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
        if (hasRole(current, "CLIENT") && !p.getRental().getUser().getId().equals(current.getId())) {
            throw new ForbiddenException("Not allowed");
        }
        return toDto(p);
    }

    @Transactional
    public PaymentDto create(Long rentalId, PaymentRequest req, User current) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (hasRole(current, "CLIENT") && !rental.getUser().getId().equals(current.getId())) {
            throw new ForbiddenException("Cannot add payment to others");
        }
        Payment p = new Payment();
        p.setRental(rental);
        p.setAmount(req.getAmount());
        p.setMethod(req.getMethod());
        p.setStatus(PaymentStatus.PAID);
        p.setPaidAt(Instant.now());
        paymentRepository.save(p);
        return toDto(p);
    }

    @Transactional
    public PaymentDto updateStatus(Long id, String status, User current) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
        if (hasRole(current, "CLIENT")) {
            throw new ForbiddenException("Clients cannot change payment status");
        }
        PaymentStatus st;
        try {
            st = PaymentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown payment status");
        }
        p.setStatus(st);
        if (st == PaymentStatus.PAID) {
            p.setPaidAt(Instant.now());
        }
        return toDto(p);
    }

    private PaymentDto toDto(Payment p) {
        return PaymentDto.builder()
                .id(p.getId())
                .rentalId(p.getRental().getId())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .method(p.getMethod())
                .paidAt(p.getPaidAt())
                .build();
    }

    private boolean hasRole(User user, String role) {
        return user.getRole().getRoleName().equalsIgnoreCase(role);
    }
}

