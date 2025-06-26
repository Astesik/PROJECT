package com.example.ioproject.payload.dtos;

import com.example.ioproject.models.Vehicle;

public class VehicleMapper {
    public static VehicleDTO toDTO(Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getProduction_year(),
                vehicle.getLicense_plate(),
                vehicle.getEngine_type(),
                vehicle.getVehicle_type(),
                vehicle.getMileage(),
                vehicle.getStatus(),
                vehicle.getImage_url(),
                vehicle.getDescription(),
                vehicle.getDaily_rate(),
                vehicle.getWeekly_rate(),
                vehicle.getMonthly_rate(),
                vehicle.getFeatures()
        );
    }
}
