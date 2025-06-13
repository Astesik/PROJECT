package com.example.ioproject.security.services;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.repository.ReservationRepository;
import com.example.ioproject.utils.ReservationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing reservations.
 * Provides methods to create, retrieve, update, and delete reservations,
 * as well as handle payment statuses and vehicle availability.
 */
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationFactory reservationFactory;

    /**
     * Creates and saves a new reservation with "PENDING" status.
     *
     * @param clientId   the ID of the client
     * @param vehicleId  the ID of the vehicle
     * @param startDate  the reservation start date (as String)
     * @param endDate    the reservation end date (as String)
     * @param cost       the total cost of the reservation
     * @return the saved {@link Reservation}
     */
    public Reservation createAndSavePendingReservation(Long clientId, int vehicleId, String startDate, String endDate, double cost) {
        Reservation reservation = reservationFactory.createPendingReservation(clientId, vehicleId, startDate, endDate, cost);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() { return reservationRepository.findAll(); }

    public Optional<Reservation> getReservationById(Long id) { return reservationRepository.findById(id); }

    public Reservation saveReservation(Reservation reservation) { return reservationRepository.save(reservation); }

    public void deleteReservation(Long id) { reservationRepository.deleteById(id); }

    /**
     * Checks whether a vehicle is available for a given date range.
     *
     * @param vehicleId  the ID of the vehicle
     * @param startDate  the start date to check
     * @param endDate    the end date to check
     * @return {@code true} if no conflicting reservations exist; {@code false} otherwise
     */
    public boolean isVehicleAvailable(int vehicleId, String startDate, String endDate) {
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(vehicleId, startDate, endDate);
        return conflicts.isEmpty();
    }

    public void markAsPaid(Long id) {
        reservationRepository.findById(id).ifPresent(res -> {
            res.setStatus("PAID");
            reservationRepository.save(res);
        });
    }

    public void markAsManual(Long id) {
        reservationRepository.findById(id).ifPresent(res -> {
            res.setStatus("MANUAL");
            reservationRepository.save(res);
        });
    }

    public void markAsCancelled(Long id) {
        reservationRepository.findById(id).ifPresent(res -> {
            res.setStatus("CANCELLED");
            reservationRepository.save(res);
        });
    }

    /**
     * Sets the Stripe session ID for a reservation.
     *
     * @param id        the reservation ID
     * @param sessionId the Stripe session ID to set
     */
    public void setStripeSessionId(Long id, String sessionId) {
        reservationRepository.findById(id).ifPresent(res -> {
            res.setStripeSessionId(sessionId);
            reservationRepository.save(res);
        });
    }

    public Optional<Reservation> findByStripeSessionId(String sessionId) {
        return reservationRepository.findByStripeSessionId(sessionId);
    }

    public List<Reservation> getReservationsByClientId(Long clientId) {
        return reservationRepository.findByClientId(clientId);
    }
}