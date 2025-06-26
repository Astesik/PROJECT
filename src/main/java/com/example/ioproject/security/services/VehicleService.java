package com.example.ioproject.security.services;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.repository.ReservationRepository;
import com.example.ioproject.repository.VehicleRepository;
import com.example.ioproject.utils.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ioproject.payload.dtos.VehicleDTO;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class that provides operations for managing {@link Vehicle} entities.
 * <p>
 * Implements the {@link IVehicleService} interface and includes logic for determining
 * the current availability status of vehicles based on reservations.
 * </p>
 */
@Service
public class VehicleService implements IVehicleService {

    @Autowired
    public VehicleRepository vehicleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Retrieves all vehicles and updates their status based on today's date and reservation status.
     * <ul>
     *     <li>If a vehicle is under maintenance, its status is not changed.</li>
     *     <li>If a vehicle has a "PAID" reservation overlapping with today, its status is set to "Rented".</li>
     *     <li>Otherwise, its status is set to "Available".</li>
     * </ul>
     *
     * @return a list of {@link Vehicle} objects with updated status
     */
    @Override
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

    /**
     * Saves the given vehicle to the repository. If the vehicle's ID is {@code 0}, it will be set to {@code null}
     * so that the database auto-generates the ID.
     *
     * @param vehicle the {@link Vehicle} to save
     * @return the saved {@link Vehicle} entity
     */
    public Vehicle saveVehicle(Vehicle vehicle) {
        // For new vehicles, ensure the ID is null to let the database auto-generate it
        if (vehicle.getId() != null && vehicle.getId() == 0) {
            vehicle.setId(null);
        }

        return vehicleRepository.save(vehicle);
    }
    public VehicleDTO toDTO(Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getProduction_year(),
                vehicle.getLicense_plate(),
                vehicle.getEngine_type(),
                vehicle.getVehicle_type(),
                vehicle.getMileage(),
                vehicle.getStatus(),
                vehicle.getImage_url(),
                vehicle.getDescription(),
                vehicle.getDaily_rate(),
                vehicle.getWeekly_rate(),
                vehicle.getMonthly_rate(),
                vehicle.getFeatures()
        );
    }

    public Vehicle fromDTO(VehicleDTO dto) {
        return new Vehicle(
                dto.getMake(),
                dto.getModel(),
                dto.getProductionYear(),
                dto.getLicensePlate(),
                dto.getEngineType(),
                dto.getVehicleType(),
                dto.getMileage(),
                dto.getStatus(),
                dto.getImageUrl(),
                dto.getDescription(),
                dto.getFeatures(),
                dto.getDailyRate(),
                dto.getWeeklyRate(),
                dto.getMonthlyRate()
        );
    }
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}
