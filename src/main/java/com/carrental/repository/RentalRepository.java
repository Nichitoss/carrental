package com.carrental.repository;

import com.carrental.model.Rental;
import com.carrental.model.RentalStatus;
import com.carrental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    List<Rental> findByUser(User user);
    List<Rental> findByStatus(RentalStatus status);
}

