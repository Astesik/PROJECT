package com.example.ioproject.controllers;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.models.User;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.repository.UserRepository;
import com.example.ioproject.security.services.ReservationService;
import com.example.ioproject.security.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        reservation.setId(id); // Ustawiamy ID, aby dokonać aktualizacji istniejącej rezerwacji
        return reservationService.saveReservation(reservation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addReservation(@RequestBody Reservation reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        reservation.setClient_id(user.getId());

        boolean available = reservationService.isVehicleAvailable(
                reservation.getVehicle_id(),
                reservation.getStart_date(),
                reservation.getEnd_date()
        );
        if (!available) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Reservation unsuccessful: the vehicle is already booked for this date.");
        }

        Reservation saved = reservationService.createAndSavePendingReservation(
                user.getId(),
                reservation.getVehicle_id(),
                reservation.getStart_date(),
                reservation.getEnd_date(),
                reservation.getCost()
        );

        return ResponseEntity.ok(saved);
    }

    // Ręcznie oznacz jako opłacone
    @PutMapping("/{id}/pay-manual")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markReservationAsManual(@PathVariable Long id) {
        reservationService.markAsManual(id);
        return ResponseEntity.ok("Reservation marked as manually paid");
    }


    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        List<Reservation> reservations = reservationService.getReservationsByClientId(user.getId());
        return ResponseEntity.ok(reservations);
    }

}