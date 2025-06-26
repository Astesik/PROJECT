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

/**
 * Proxy class for {@link VehicleService} that adds caching behavior for vehicle data.
 * <p>
 * Implements {@link IVehicleService} and delegates calls to the real service
 * while caching the result of {@code getAllVehicles()} for a specified interval.
 * </p>
 */
@Service
@Primary
public class VehicleServiceProxy implements IVehicleService{


    private final VehicleService realService;
    private List<Vehicle> cachedVehicles = new ArrayList<>();
    private LocalDateTime lastUpdated = LocalDateTime.MIN;
    private final Duration refreshInterval = Duration.ofSeconds(180);

    /**
     * Constructs a new {@code VehicleServiceProxy} with a reference to the real service.
     *
     * @param realService the actual {@link VehicleService} implementation to delegate to
     */
    public VehicleServiceProxy(VehicleService realService) {
        this.realService = realService;
        System.out.println("✅ VehicleServiceProxy został utworzony!");
    }

    /**
     * Returns a list of all vehicles, using cached data if the cache is still valid.
     * If the cache has expired, it fetches fresh data from the real service.
     *
     * @return a list of {@link Vehicle} objects
     */
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
