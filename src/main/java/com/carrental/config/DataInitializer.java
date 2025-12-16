package com.carrental.config;

import com.carrental.model.Role;
import com.carrental.model.User;
import com.carrental.model.Vehicle;
import com.carrental.model.VehicleStatus;
import com.carrental.repository.RoleRepository;
import com.carrental.repository.UserRepository;
import com.carrental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               VehicleRepository vehicleRepository,
                               PasswordEncoder passwordEncoder,
                               @Value("${app.admin.username}") String adminUsername,
                               @Value("${app.admin.password}") String adminPassword,
                               @Value("${app.admin.email}") String adminEmail,
                               @Value("${app.admin.first-name}") String adminFirst,
                               @Value("${app.admin.last-name}") String adminLast) {
        return args -> {
            roleRepository.findByRoleName("ADMIN").ifPresent(role -> {
                if (userRepository.findByUsername(adminUsername).isEmpty()) {
                    User admin = User.builder()
                            .username(adminUsername)
                            .password(passwordEncoder.encode(adminPassword))
                            .email(adminEmail)
                            .firstName(adminFirst)
                            .lastName(adminLast)
                            .role(role)
                            .active(true)
                            .build();
                    userRepository.save(admin);
                }
            });

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

