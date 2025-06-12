package com.example.ioproject.utils;

import com.example.ioproject.models.Vehicle;
import com.example.ioproject.security.services.VehicleService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Primary
public class VehicleServiceProxy implements IVehicleService{


    private final VehicleService realService;
    private List<Vehicle> cachedVehicles = new ArrayList<>();
    private LocalDateTime lastUpdated = LocalDateTime.MIN;
    private final Duration refreshInterval = Duration.ofSeconds(180);

    public VehicleServiceProxy(VehicleService realService) {
        this.realService = realService;
        System.out.println("✅ VehicleServiceProxy został utworzony!");
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        if (Duration.between(lastUpdated, LocalDateTime.now()).compareTo(refreshInterval) > 0) {
            this.cachedVehicles = realService.getAllVehicles();
            this.lastUpdated = LocalDateTime.now();
        }
        return cachedVehicles;
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return realService.vehicleRepository.findById(id);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        // For new vehicles, ensure the ID is null to let the database auto-generate it
        if (vehicle.getId() != null && vehicle.getId() == 0) {
            vehicle.setId(null);
        }

        return realService.vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        realService.vehicleRepository.deleteById(id);
    }
}
