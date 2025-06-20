package com.example.ioproject.models;

import com.example.ioproject.payload.dtos.MaintenanceTaskDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a maintenance task associated with a specific vehicle.
 * Includes details such as the description, duration, cost, and completion status of the task.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "maintenance_tasks")
public class MaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    private String description;
    private String start_date;
    private String end_date;
    private Double cost;
    private Boolean done;

    public MaintenanceTask(MaintenanceTaskDTO dto, Vehicle vehicle) {
        this.vehicle = vehicle;
        this.description = dto.getDescription();
        this.start_date = dto.getStart_date();
        this.end_date = dto.getEnd_date();
        this.cost = dto.getCost();
        this.done = dto.getDone();
    }


}
