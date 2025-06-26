package com.example.ioproject.payload.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String make;
    private String model;
    private int production_year;
    private String license_plate;
    private String engine_type;
    private String vehicle_type;
    private int mileage;
    private String status;
    private String image_url;
    private String description;
    private double daily_rate;
    private double weekly_rate;
    private double monthly_rate;
    private List<String> features;
}
