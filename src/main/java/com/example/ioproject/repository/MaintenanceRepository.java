package com.example.ioproject.repository;

import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link MaintenanceTask} entities.
 *
 * Extends JpaRepository to provide CRUD operations, pagination, and sorting for MaintenanceTask objects.
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceTask, Long> {
}
