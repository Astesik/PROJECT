package com.example.ioproject.security.services;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.repository.ReservationRepository;
import com.example.ioproject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Vehicle vehicle : vehicles) {

            if ("Under Maintenance".equalsIgnoreCase(vehicle.getStatus())) {
                continue;
            }

            List<Reservation> reservations = reservationRepository.findByVehicleId(vehicle.getId());

            boolean isRentedToday = reservations.stream().anyMatch(res -> {
                LocalDate start = LocalDate.parse(res.getStart_date());
                LocalDate end = LocalDate.parse(res.getEnd_date());
                return !today.isBefore(start) && !today.isAfter(end) && res.getStatus().equals("PAID");
            });

            if (isRentedToday) {
                vehicle.setStatus("Rented");
            } else {
                vehicle.setStatus("Available");
            }
        }

        return vehicles;
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        // For new vehicles, ensure the ID is null to let the database auto-generate it
        if (vehicle.getId() != null && vehicle.getId() == 0) {
            vehicle.setId(null);
        }

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}