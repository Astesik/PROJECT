package com.example.ioproject.security.services;

import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    public List<MaintenanceTask> getAllMaintenanceTasks() {
        return maintenanceRepository.findAll();
    }

    public Optional<MaintenanceTask> getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id);
    }

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