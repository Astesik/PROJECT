package com.example.ioproject.models;

import jakarta.persistence.*;

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
    private String type;
    private int mileage;
    private String technical_condition;
    private double price_per_day;
    private String imagePath;

    public Vehicle(){}

    public Vehicle(String make, String model,
                   int production_year, String license_plate,
                   String type, int mileage, String technical_condition,
                   double price_per_day, String imagePath) {
        this.make = make;
        this.model = model;
        this.production_year = production_year;
        this.license_plate = license_plate;
        this.type = type;
        this.mileage = mileage;
        this.technical_condition = technical_condition;
        this.price_per_day = price_per_day;
        this.imagePath = imagePath;
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

    public void setLicense_plate(String license_plate) { this.license_plate = license_plate; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public int getMileage() { return mileage; }

    public void setMileage(int mileage) { this.mileage = mileage; }

    public String getTechnical_condition() { return technical_condition; }

    public void setTechnical_condition(String technical_condition) { this.technical_condition = technical_condition; }

    public double getPrice_per_day() { return price_per_day; }

    public void setPrice_per_day(int price_per_day) {this.price_per_day = price_per_day; }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
