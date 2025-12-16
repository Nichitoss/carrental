package com.example.carrental.service;

import com.example.carrental.dto.VehicleDto;
import com.example.carrental.entity.Vehicle;
import com.example.carrental.model.VehicleStatus;
import com.example.carrental.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleDto> getAll(String status, String query) {
        return vehicleRepository.findAll().stream()
                .filter(v -> status == null || v.getStatus().name().equalsIgnoreCase(status))
                .filter(v -> query == null || (v.getManufacturer() + " " + v.getModel()).toLowerCase().contains(query.toLowerCase()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VehicleDto> getAvailable() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).stream()
                .map(this::toDto).toList();
    }

    public VehicleDto getById(Long id) {
        return vehicleRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
    }

    @Transactional
    public VehicleDto create(VehicleDto dto) {
        Vehicle v = new Vehicle();
        v.setLicensePlate(dto.licensePlate());
        v.setManufacturer(dto.manufacturer());
        v.setModel(dto.model());
        v.setYear(dto.year());
        v.setDailyPrice(dto.dailyPrice());
        v.setStatus(VehicleStatus.valueOf(dto.status()));
        v.setColor(dto.color());
        v.setMileage(dto.mileage());
        return toDto(vehicleRepository.save(v));
    }

    @Transactional
    public VehicleDto update(Long id, VehicleDto dto) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        v.setManufacturer(dto.manufacturer());
        v.setModel(dto.model());
        v.setYear(dto.year());
        v.setDailyPrice(dto.dailyPrice());
        v.setStatus(VehicleStatus.valueOf(dto.status()));
        v.setColor(dto.color());
        v.setMileage(dto.mileage());
        return toDto(vehicleRepository.save(v));
    }

    @Transactional
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    private VehicleDto toDto(Vehicle v) {
        return new VehicleDto(
                v.getId(),
                v.getLicensePlate(),
                v.getManufacturer(),
                v.getModel(),
                v.getYear(),
                v.getDailyPrice(),
                v.getStatus().name(),
                v.getColor(),
                v.getMileage()
        );
    }
}

