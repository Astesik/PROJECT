package com.example.ioproject.payload.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    private Long id;
    private String make;
    private String model;
    private int productionYear;
    private String licensePlate;
    private String engineType;
    private String vehicleType;
    private int mileage;
    private String status;
    private String imageUrl;
    private String description;
    private double dailyRate;
    private double weeklyRate;
    private double monthlyRate;
    private List<String> features;
}
