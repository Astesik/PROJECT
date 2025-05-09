package com.example.ioproject.repository;

import com.example.ioproject.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
//    Optional<Vehicle> findByImage(String image);
}
