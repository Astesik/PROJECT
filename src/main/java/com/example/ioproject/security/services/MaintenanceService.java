package com.example.ioproject.security.services;

import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.payload.dtos.MaintenanceTaskDTO;
import com.example.ioproject.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing vehicle maintenance tasks.
 * <p>
 * Provides methods to retrieve, save, and delete maintenance records.
 */
@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    public List<MaintenanceTaskDTO> getAllMaintenanceTasks() {
        List<MaintenanceTask> tasks = maintenanceRepository.findAll();

        return tasks.stream()
                .map(task -> new MaintenanceTaskDTO(
                        task.getId(),
                        task.getVehicle().getId(),
                        task.getVehicle().getLicense_plate(),
                        task.getDescription(),
                        task.getStart_date(),
                        task.getEnd_date(),
                        task.getCost(),
                        task.getDone()
                ))
                .collect(Collectors.toList());
    }

    public Optional<MaintenanceTask> getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id);
    }

    /**
     * Saves a maintenance task to the database.
     * <p>
     * If the task has an ID of 0, it is treated as a new entity and the ID is reset to {@code null}.
     *
     * @param maintenanceTask the maintenance task to save
     * @return the saved {@link MaintenanceTask} entity
     */
    public MaintenanceTask saveMaintenance(MaintenanceTask maintenanceTask) {
        if (maintenanceTask.getId() != null && maintenanceTask.getId() == 0) {
            maintenanceTask.setId(null);
        }

        return maintenanceRepository.save(maintenanceTask);
    }

    public void deleteMaintenanceTask(Long id) {
        maintenanceRepository.deleteById(id);
    }
}