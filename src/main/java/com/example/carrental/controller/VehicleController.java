package com.example.carrental.controller;

import com.example.carrental.dto.VehicleDto;
import com.example.carrental.model.RoleName;
import com.example.carrental.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<VehicleDto> all(@RequestParam(required = false) String status,
                                @RequestParam(required = false, name = "q") String query) {
        return vehicleService.getAll(status, query);
    }

    @GetMapping("/{id}")
    public VehicleDto get(@PathVariable Long id) {
        return vehicleService.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public VehicleDto create(@RequestBody VehicleDto dto) {
        return vehicleService.create(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public VehicleDto update(@PathVariable Long id, @RequestBody VehicleDto dto) {
        return vehicleService.update(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

