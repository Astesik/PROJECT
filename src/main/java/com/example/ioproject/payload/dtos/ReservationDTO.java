package com.example.ioproject.payload.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Long id;
    private Long client_id;
    private int vehicle_id;
    private String license_plate;
    private String start_date;
    private String end_date;
    private double cost;
    private String status;
    private String stripeSessionId;
}
