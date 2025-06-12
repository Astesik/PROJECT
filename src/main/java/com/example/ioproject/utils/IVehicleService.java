package com.example.ioproject.utils;

import com.example.ioproject.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface IVehicleService {
    List<Vehicle> getAllVehicles();
    Optional<Vehicle> getVehicleById(Long id);
    Vehicle saveVehicle(Vehicle vehicle);
    void deleteVehicle(Long id);
}
