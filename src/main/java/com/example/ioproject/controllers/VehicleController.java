package com.example.ioproject.controllers;

import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.payload.dtos.MaintenanceTaskDTO;
import com.example.ioproject.payload.dtos.VehicleDTO;
import com.example.ioproject.security.services.MaintenanceService;
import com.example.ioproject.security.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping("/get")
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles()
                .stream()
                .map(vehicleService::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(id);
        return vehicleOpt.map(vehicle -> ResponseEntity.ok(vehicleService.toDTO(vehicle)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<VehicleDTO> addVehicle(@RequestBody VehicleDTO dto) {
        Vehicle saved = vehicleService.saveVehicle(vehicleService.fromDTO(dto));
        return new ResponseEntity<>(vehicleService.toDTO(saved), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO dto) {
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(id);
        if (vehicleOpt.isPresent()) {
            Vehicle updated = vehicleService.fromDTO(dto);
            updated.setId(id);
            Vehicle saved = vehicleService.saveVehicle(updated);
            return ResponseEntity.ok(vehicleService.toDTO(saved));
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
    public ResponseEntity<MaintenanceTask> createMaintenanceTask(@RequestBody MaintenanceTaskDTO dto) {
        Vehicle vehicle = vehicleService.getVehicleById(dto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        MaintenanceTask maintenanceTask = new MaintenanceTask(dto, vehicle);
        MaintenanceTask savedMaintenance = maintenanceService.saveMaintenance(maintenanceTask);

        return new ResponseEntity<>(savedMaintenance, HttpStatus.CREATED);
    }

    @GetMapping("/maintenance-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public List<MaintenanceTaskDTO> getMaintenanceTasks() {
        return maintenanceService.getAllMaintenanceTasks();
    }

    @PutMapping("/maintenance-tasks/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<MaintenanceTask> updateMaintenanceTask(@PathVariable Long id, @RequestBody MaintenanceTaskDTO dto) {
        Optional<MaintenanceTask> maintenanceData = maintenanceService.getMaintenanceById(dto.getId());

        if (maintenanceData.isPresent()) {
            MaintenanceTask maintenanceTask = new MaintenanceTask(dto, maintenanceData.get().getVehicle());
            maintenanceTask.setId(id);
            MaintenanceTask updatedTask = maintenanceService.saveMaintenance(maintenanceTask);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
