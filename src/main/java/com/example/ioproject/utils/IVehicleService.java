package com.example.ioproject.utils;

import com.example.ioproject.models.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining basic operations for managing {@link Vehicle} entities.
 * <p>
 * Provides CRUD operations such as retrieving all vehicles, getting a vehicle by ID,
 * saving a vehicle, and deleting a vehicle.
 * </p>
 */
public interface IVehicleService {
    List<Vehicle> getAllVehicles();
    Optional<Vehicle> getVehicleById(Long id);
    Vehicle saveVehicle(Vehicle vehicle);
    void deleteVehicle(Long id);
}
