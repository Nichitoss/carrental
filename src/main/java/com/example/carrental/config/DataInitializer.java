package com.example.carrental.config;

import com.example.carrental.entity.Role;
import com.example.carrental.entity.User;
import com.example.carrental.entity.Vehicle;
import com.example.carrental.model.RoleName;
import com.example.carrental.model.VehicleStatus;
import com.example.carrental.repository.RoleRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepository,
                           UserRepository userRepository,
                           VehicleRepository vehicleRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin.username}") String adminUsername,
                           @Value("${app.admin.password}") String adminPassword,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.first-name}") String adminFirst,
                           @Value("${app.admin.last-name}") String adminLast) {
        return args -> {
            Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN).orElseThrow();
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setEmail(adminEmail);
                admin.setFirstName(adminFirst);
                admin.setLastName(adminLast);
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
            if (vehicleRepository.count() == 0) {
                Vehicle v1 = new Vehicle();
                v1.setLicensePlate("A111AA");
                v1.setManufacturer("Toyota");
                v1.setModel("Camry");
                v1.setYear(2022);
                v1.setDailyPrice(new BigDecimal("70.00"));
                v1.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(v1);

                Vehicle v2 = new Vehicle();
                v2.setLicensePlate("B222BB");
                v2.setManufacturer("Tesla");
                v2.setModel("Model 3");
                v2.setYear(2023);
                v2.setDailyPrice(new BigDecimal("120.00"));
                v2.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(v2);
            }
        };
    }
}

