package com.example.ioproject.security.services;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() { return reservationRepository.findAll(); }
    public Optional<Reservation> getReservationById(Long id) { return reservationRepository.findById(id); }
    public Reservation saveReservation(Reservation reservation) { return reservationRepository.save(reservation); }
    public void deleteReservation(Long id) { reservationRepository.deleteById(id); }

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