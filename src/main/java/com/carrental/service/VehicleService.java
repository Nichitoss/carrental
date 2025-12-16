package com.carrental.service;

import com.carrental.dto.vehicle.VehicleDto;
import com.carrental.dto.vehicle.VehicleRequest;
import com.carrental.exception.NotFoundException;
import com.carrental.model.Vehicle;
import com.carrental.model.VehicleStatus;
import com.carrental.repository.VehicleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> list(String status, String search, String sort) {
        Specification<Vehicle> spec = Specification.where(null);
        if (status != null && !status.isBlank()) {
            try {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("status"), VehicleStatus.valueOf(status)));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("manufacturer")), like),
                    cb.like(cb.lower(root.get("model")), like),
                    cb.like(cb.lower(root.get("licensePlate")), like)
            ));
        }
        Sort sortSpec = Sort.by("manufacturer").ascending();
        if (sort != null && !sort.isBlank()) {
            if (sort.equalsIgnoreCase("price")) sortSpec = Sort.by("dailyPrice").ascending();
            else if (sort.equalsIgnoreCase("year")) sortSpec = Sort.by("year").descending();
        }
        return vehicleRepository.findAll(spec, sortSpec).stream().map(this::toDto).toList();
    }

    public VehicleDto get(Long id) {
        return toDto(load(id));
    }

    @Transactional
    public VehicleDto create(VehicleRequest req) {
        Vehicle v = new Vehicle();
        fill(v, req);
        return toDto(vehicleRepository.save(v));
    }

    @Transactional
    public VehicleDto update(Long id, VehicleRequest req) {
        Vehicle v = load(id);
        fill(v, req);
        return toDto(vehicleRepository.save(v));
    }

    @Transactional
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    private Vehicle load(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
    }

    private void fill(Vehicle v, VehicleRequest req) {
        v.setLicensePlate(req.getLicensePlate());
        v.setManufacturer(req.getManufacturer());
        v.setModel(req.getModel());
        v.setYear(req.getYear());
        v.setDailyPrice(req.getDailyPrice());
        if (req.getStatus() != null) {
            v.setStatus(VehicleStatus.valueOf(req.getStatus()));
        }
        v.setColor(req.getColor());
        v.setMileage(req.getMileage());
    }

    public VehicleDto toDto(Vehicle v) {
        return VehicleDto.builder()
                .id(v.getId())
                .licensePlate(v.getLicensePlate())
                .manufacturer(v.getManufacturer())
                .model(v.getModel())
                .year(v.getYear())
                .dailyPrice(v.getDailyPrice())
                .status(v.getStatus().name())
                .color(v.getColor())
                .mileage(v.getMileage())
                .build();
    }
}

