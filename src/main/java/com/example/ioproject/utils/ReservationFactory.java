package com.example.ioproject.utils;

import com.example.ioproject.models.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationFactory {
    public Reservation createPendingReservation(Long clientId, int vehicleId, String startDate, String endDate, double cost) {
        Reservation reservation = new Reservation();
        reservation.setClient_id(clientId);
        reservation.setVehicle_id(vehicleId);
        reservation.setStart_date(startDate);
        reservation.setEnd_date(endDate);
        reservation.setCost(cost);
        reservation.setStatus("PENDING");
        return reservation;
    }
}
