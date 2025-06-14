package com.example.ioproject.models;

import jakarta.persistence.*;

/**
 * Represents a maintenance task associated with a specific vehicle.
 * Includes details such as the description, duration, cost, and completion status of the task.
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Boolean getDone() {return done;}

    public void setDone(Boolean done) {this.done = done;}

    @Override
    public String toString() {
        return "MaintenanceTask{" +
                "id=" + id +
                ", vehicle=" + vehicle +
                ", description='" + description + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", cost=" + cost +
                ", done=" + done +
                '}';
    }
}
