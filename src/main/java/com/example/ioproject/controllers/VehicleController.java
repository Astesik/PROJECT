package com.example.ioproject.controllers;

import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.security.services.MaintenanceService;
import com.example.ioproject.security.services.VehicleService;
import com.example.ioproject.utils.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller responsible for managing vehicles and their maintenance tasks.
 * <p>
 * Provides endpoints to retrieve, add, update, and delete vehicles,
 * as well as create and update maintenance tasks. Access to certain operations
 * is restricted to specific user roles (e.g., ADMIN, MODERATOR, MECHANIC).
 * </p>
 */
@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private MaintenanceService maintenanceService;

    /**
     * Retrieves all vehicles from the system.
     * Publicly accessible.
     *
     * @return a list of all {@link Vehicle} objects
     */
    @GetMapping("/get")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    /**
     * Retrieves a single vehicle by its ID.
     * Publicly accessible.
     *
     * @param id the ID of the vehicle to retrieve
     * @return an {@link Optional} containing the vehicle, if found
     */
    @GetMapping("/get/{id}")
    public Optional<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    /**
     * Adds a new vehicle to the system.
     * Only accessible to users with the ADMIN or MODERATOR role.
     *
     * @param vehicle the {@link Vehicle} object to be saved
     * @return the saved vehicle with HTTP 201 Created status
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

    /**
     * Updates an existing vehicle based on its ID.
     * Only accessible to users with the ADMIN or MODERATOR role.
     *
     * @param id      the ID of the vehicle to update
     * @param vehicle the updated vehicle data
     * @return the updated {@link Vehicle} object or 404 if not found
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        Optional<Vehicle> vehicleData = vehicleService.getVehicleById(id);

        if (vehicleData.isPresent()) {
            vehicle.setId(id);
            Vehicle updatedVehicle = vehicleService.saveVehicle(vehicle);
            return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a vehicle from the system.
     * Only accessible to users with the ADMIN or MODERATOR role.
     *
     * @param id the ID of the vehicle to delete
     * @return HTTP 204 No Content on success, or 500 Internal Server Error on failure
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<HttpStatus> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new maintenance task for a vehicle.
     * Only accessible to users with the ADMIN or MECHANIC role.
     *
     * @param maintenanceTask the maintenance task details
     * @return the created {@link MaintenanceTask} object
     */
    @PostMapping("/maintenance-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<MaintenanceTask> createMaintenanceTask(@RequestBody MaintenanceTask maintenanceTask) {
        MaintenanceTask savedMaintenance = maintenanceService.saveMaintenance(maintenanceTask);
        return new ResponseEntity<>(savedMaintenance, HttpStatus.CREATED);
    }

    /**
     * Retrieves all maintenance tasks for vehicles.
     * Only accessible to users with the ADMIN or MECHANIC role.
     *
     * @return a list of all {@link MaintenanceTask} objects
     */
    @GetMapping("/maintenance-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public List<MaintenanceTask> getMaintenanceTasks() {
        return maintenanceService.getAllMaintenanceTasks();
    }

    /**
     * Updates an existing maintenance task by its ID.
     * Only accessible to users with the ADMIN or MECHANIC role.
     *
     * @param id              the ID of the maintenance task to update
     * @param maintenanceTask the updated maintenance task details
     * @return the updated {@link MaintenanceTask} or 404 if not found
     */
    @PutMapping("/maintenance-tasks/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<MaintenanceTask> updateMaintenanceTAsk(@PathVariable Long id, @RequestBody MaintenanceTask maintenanceTask) {
        Optional<MaintenanceTask> maintenanceData = maintenanceService.getMaintenanceById(id);

        if (maintenanceData.isPresent()) {
            maintenanceTask.setId(id);
            MaintenanceTask updatedTask = maintenanceService.saveMaintenance(maintenanceTask);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
