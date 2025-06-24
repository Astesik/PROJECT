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
    private Long clientId;
    private int vehicleId;
    private String startDate;
    private String endDate;
    private double cost;
    private String status;
    private String stripeSessionId;
}
