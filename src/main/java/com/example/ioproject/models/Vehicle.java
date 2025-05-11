package com.example.ioproject.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

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

    // Adding pricing fields
    private double daily_rate;
    private double weekly_rate;
    private double monthly_rate;

    @ElementCollection
    @CollectionTable(name = "vehicle_features", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "feature")
    private List<String> features;

    // Constructors
    public Vehicle() {
    }

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

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getMake() { return make; }

    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public int getProduction_year() { return production_year; }

    public void setProduction_year(int production_year) { this.production_year = production_year; }

    public String getLicense_plate() { return license_plate; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLicense_plate(String license_plate) { this.license_plate = license_plate; }

    public String getVehicle_type() { return vehicle_type; }

    public void setVehicle_type(String vehicle_type) { this.vehicle_type = vehicle_type; }

    public String getEngine_type() { return engine_type; }

    public void setEngine_type(String engine_type) { this.engine_type = engine_type; }

    public int getMileage() { return mileage; }

    public void setMileage(int mileage) { this.mileage = mileage; }

    public double getDaily_rate() { return daily_rate; }

    public void setDaily_rate(int price_per_day) {this.daily_rate = price_per_day; }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_path) {
        this.image_url = image_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public double getWeekly_rate() { return weekly_rate; }

    public void setWeekly_rate(int price_per_day) {this.weekly_rate = price_per_day; }


    public double getMonthly_rate() { return monthly_rate; }

    public void setMonthly_rate(int price_per_day) {this.monthly_rate = price_per_day; }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
