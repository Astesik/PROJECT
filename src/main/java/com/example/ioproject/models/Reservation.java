package com.example.ioproject.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a reservation for a vehicle.
 * Contains details about the rental period, associated user and vehicle,
 * payment status, and cost.
 */
@Getter
@Setter
@Entity
@Table( name = "reservations" )
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private Long client_id;
    private int vehicle_id;
    private String start_date;
    private String end_date;
    private double cost;
    private String status; // "PENDING", "PAID", "MANUAL", "CANCELLED"
    private String stripeSessionId;

}