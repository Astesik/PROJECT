package com.example.ioproject.repository;

import com.example.ioproject.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Vehicle} entities.
 *
 * Extends JpaRepository to provide CRUD operations, pagination, and sorting for Vehicle objects.
 */
 @Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
