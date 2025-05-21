package com.example.ioproject.repository;

import com.example.ioproject.models.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceTask, Long> {
}
