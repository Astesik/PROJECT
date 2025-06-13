package com.example.ioproject.utils;

import com.example.ioproject.models.Reservation;
import org.springframework.stereotype.Component;

/**
 * Factory class responsible for creating {@link Reservation} objects with different statuses.
 * <p>
 * Provides methods to create reservations with statuses "PENDING" and "PAID".
 * </p>
 */
@Component
public class ReservationFactory {

    /**
     * Creates a new reservation with status "PENDING".
     *
     * @param clientId  the ID of the client making the reservation
     * @param vehicleId the ID of the vehicle being reserved
     * @param startDate the start date of the reservation as a String
     * @param endDate   the end date of the reservation as a String
     * @param cost      the cost of the reservation
     * @return a new {@link Reservation} instance with status set to "PENDING"
     */
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

    /**
     * Creates a new reservation with status "PAID".
     *
     * @param clientId  the ID of the client making the reservation
     * @param vehicleId the ID of the vehicle being reserved
     * @param startDate the start date of the reservation as a String
     * @param endDate   the end date of the reservation as a String
     * @param cost      the cost of the reservation
     * @return a new {@link Reservation} instance with status set to "PAID"
     */
    public Reservation createPaidReservation(Long clientId, int vehicleId, String startDate, String endDate, double cost) {
        Reservation reservation = new Reservation();
        reservation.setClient_id(clientId);
        reservation.setVehicle_id(vehicleId);
        reservation.setStart_date(startDate);
        reservation.setEnd_date(endDate);
        reservation.setCost(cost);
        reservation.setStatus("PAID");
        return reservation;
    }
}
