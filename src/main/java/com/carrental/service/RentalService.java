package com.carrental.service;

import com.carrental.dto.rental.RentalDto;
import com.carrental.dto.rental.RentalRequest;
import com.carrental.dto.rental.RentalStatusUpdateRequest;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ForbiddenException;
import com.carrental.exception.NotFoundException;
import com.carrental.model.*;
import com.carrental.repository.RentalRepository;
import com.carrental.repository.UserRepository;
import com.carrental.repository.VehicleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRepository rentalRepository,
                         VehicleRepository vehicleRepository,
                         UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<RentalDto> listForUser(User current) {
        if (hasRole(current, "CLIENT")) {
            return rentalRepository.findByUser(current).stream().map(this::toDto).toList();
        }
        return rentalRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<RentalDto> search(String status, Long userId, Authentication auth) {
        Specification<Rental> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), RentalStatus.valueOf(status)));
        }
        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"))) {
            throw new ForbiddenException("Not allowed to search rentals");
        }
        return rentalRepository.findAll(spec).stream().map(this::toDto).toList();
    }

    @Transactional
    public RentalDto create(RentalRequest req, User current) {
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
        if (vehicle.getStatus() == VehicleStatus.RENTED) {
            throw new BadRequestException("Vehicle already rented");
        }
        User renter = current;
        if (req.getUserId() != null) {
            if (!hasRole(current, "MANAGER") && !hasRole(current, "ADMIN")) {
                throw new ForbiddenException("Only manager/admin can create rental for others");
            }
            renter = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
        }
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        Rental rental = new Rental();
        rental.setUser(renter);
        rental.setVehicle(vehicle);
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setStartDate(req.getStartDate());
        rental.setEndDate(req.getEndDate());
        rental.setTotalPrice(calculatePrice(vehicle.getDailyPrice(), req.getStartDate(), req.getEndDate()));
        if (hasRole(current, "MANAGER")) {
            rental.setManager(current);
        }
        vehicle.setStatus(VehicleStatus.RENTED);
        rentalRepository.save(rental);
        return toDto(rental);
    }

    @Transactional
    public RentalDto updateStatus(Long id, RentalStatusUpdateRequest req, User current) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (hasRole(current, "CLIENT") && !rental.getUser().getId().equals(current.getId())) {
            throw new ForbiddenException("Cannot change other user's rental");
        }
        RentalStatus newStatus = RentalStatus.valueOf(req.getStatus());
        rental.setStatus(newStatus);
        if (newStatus == RentalStatus.CANCELLED || newStatus == RentalStatus.COMPLETED) {
            rental.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        }
        return toDto(rental);
    }

    @Transactional
    public void delete(Long id, User current) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (hasRole(current, "CLIENT") && !rental.getUser().getId().equals(current.getId())) {
            throw new ForbiddenException("Cannot delete other user's rental");
        }
        rentalRepository.delete(rental);
    }

    public RentalDto get(Long id, User current) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (hasRole(current, "CLIENT") && !rental.getUser().getId().equals(current.getId())) {
            throw new ForbiddenException("No access to this rental");
        }
        return toDto(rental);
    }

    private boolean hasRole(User user, String role) {
        return user.getRole().getRoleName().equalsIgnoreCase(role);
    }

    private BigDecimal calculatePrice(BigDecimal daily, LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        return daily.multiply(BigDecimal.valueOf(days));
    }

    public RentalDto toDto(Rental r) {
        return RentalDto.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .username(r.getUser().getUsername())
                .vehicleId(r.getVehicle().getId())
                .vehicleModel(r.getVehicle().getModel())
                .status(r.getStatus().name())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .totalPrice(r.getTotalPrice())
                .managerId(r.getManager() != null ? r.getManager().getId() : null)
                .build();
    }
}

