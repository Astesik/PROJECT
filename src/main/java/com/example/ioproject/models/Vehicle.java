package com.example.ioproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * Represents a vehicle available in the rental system.
 * This class is mapped to the "vehicles" table in the database.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table( name = "vehicles" )
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(length = 1000)
    private String description;

    private double daily_rate;
    private double weekly_rate;
    private double monthly_rate;

    @ElementCollection
    @CollectionTable(name = "vehicle_features", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "feature")
    private List<String> features;


    public Vehicle(String make, String model, int production_year, String license_plate,
                   String engineType, String vehicle_type, int mileage, String status,
                   String image_url, String description, List<String> features,
                   double daily_rate, double weekly_rate, double monthly_rate) {
        this.make = make;
        this.model = model;
        this.production_year = production_year;
        this.license_plate = license_plate;
        this.engine_type = engineType;
        this.vehicle_type = vehicle_type;
        this.mileage = mileage;
        this.status = status;
        this.image_url = image_url;
        this.description = description;
        this.features = features;
        this.daily_rate = daily_rate;
        this.weekly_rate = weekly_rate;
        this.monthly_rate = monthly_rate;
    }
}