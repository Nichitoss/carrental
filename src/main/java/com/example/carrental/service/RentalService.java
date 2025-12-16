package com.example.carrental.service;

import com.example.carrental.dto.RentalDto;
import com.example.carrental.entity.Rental;
import com.example.carrental.entity.User;
import com.example.carrental.entity.Vehicle;
import com.example.carrental.model.RentalStatus;
import com.example.carrental.repository.RentalRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository,
                         UserRepository userRepository,
                         VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<RentalDto> findAll() {
        return rentalRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<RentalDto> findByUser(Long userId) {
        return rentalRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    @Transactional
    public RentalDto createForUser(Long userId, Long vehicleId, RentalDto dto, Long managerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setVehicle(vehicle);
        if (managerId != null) {
            userRepository.findById(managerId).ifPresent(rental::setManager);
        }
        rental.setStartDate(dto.startDate());
        rental.setEndDate(dto.endDate());
        long days = ChronoUnit.DAYS.between(dto.startDate(), dto.endDate());
        rental.setTotalPrice(vehicle.getDailyPrice().multiply(BigDecimal.valueOf(Math.max(days, 1))));
        rental.setStatus(RentalStatus.ACTIVE);
        return toDto(rentalRepository.save(rental));
    }

    @Transactional
    public RentalDto updateStatus(Long rentalId, RentalStatus status, Long managerId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        if (managerId != null && rental.getManager() == null) {
            userRepository.findById(managerId).ifPresent(rental::setManager);
        }
        rental.setStatus(status);
        return toDto(rentalRepository.save(rental));
    }

    @Transactional
    public void delete(Long rentalId) {
        rentalRepository.deleteById(rentalId);
    }

    private RentalDto toDto(Rental rental) {
        return new RentalDto(
                rental.getId(),
                rental.getUser().getId(),
                rental.getVehicle().getId(),
                rental.getManager() != null ? rental.getManager().getId() : null,
                rental.getStatus().name(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getTotalPrice()
        );
    }
}

