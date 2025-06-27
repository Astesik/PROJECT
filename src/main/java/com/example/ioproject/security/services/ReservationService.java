package com.example.ioproject.security.services;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.payload.dtos.ReservationDTO;
import com.example.ioproject.repository.ReservationRepository;
import com.example.ioproject.repository.VehicleRepository;
import com.example.ioproject.utils.ReservationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationFactory reservationFactory;

    @Autowired
    private VehicleRepository vehicleRepository; // <== Dodane do pobierania licensePlate

    public Reservation createAndSavePendingReservation(Long clientId, int vehicleId, String startDate, String endDate, double cost) {
        Reservation reservation = reservationFactory.createPendingReservation(clientId, vehicleId, startDate, endDate, cost);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

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

    // =========================
    // DTO mapping
    // =========================

    public ReservationDTO toDTO(Reservation res) {
        String licensePlate = "";

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById((long) res.getVehicle_id());
        if (vehicleOpt.isPresent()) {
            licensePlate = vehicleOpt.get().getLicense_plate();
        }

        return new ReservationDTO(
                res.getId(),
                res.getClient_id(),
                res.getVehicle_id(),
                licensePlate,
                res.getStart_date(),
                res.getEnd_date(),
                res.getCost(),
                res.getStatus(),
                res.getStripeSessionId()
        );
    }

    public Reservation fromDTO(ReservationDTO dto) {
        Reservation res = new Reservation();
        res.setId(dto.getId());
        res.setClient_id(dto.getClient_id());
        res.setVehicle_id(dto.getVehicle_id());
        res.setStart_date(dto.getStart_date());
        res.setEnd_date(dto.getEnd_date());
        res.setCost(dto.getCost());
        res.setStatus(dto.getStatus());
        res.setStripeSessionId(dto.getStripeSessionId());
        return res;
    }
}
