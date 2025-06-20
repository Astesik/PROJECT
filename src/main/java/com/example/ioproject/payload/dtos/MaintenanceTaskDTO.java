package com.example.ioproject.payload.dtos;

import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.models.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class MaintenanceTaskDTO {

    private Long id;
    private Long vehicleId;
    private String licensePlate;
    private String description;
    private String start_date;
    private String end_date;
    private Double cost;
    private Boolean done;

}
