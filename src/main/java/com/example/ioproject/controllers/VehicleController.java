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

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    MaintenanceService maintenanceService;

    @GetMapping("/get")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/get/{id}")
    public Optional<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    // Endpoint: Dodaj nowy pojazd (tylko dla użytkowników z rolą ADMIN lub EMPLOYEE)
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

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

    @PostMapping("/maintenance-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<MaintenanceTask> createMaintenanceTask(@RequestBody MaintenanceTask maintenanceTask) {
        MaintenanceTask savedMaintenance = maintenanceService.saveMaintenance(maintenanceTask);
        return new ResponseEntity<>(savedMaintenance, HttpStatus.CREATED);
    }

    @GetMapping("/maintenance-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public List<MaintenanceTask> getMaintenanceTasks() {
        return maintenanceService.getAllMaintenanceTasks();
    }

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