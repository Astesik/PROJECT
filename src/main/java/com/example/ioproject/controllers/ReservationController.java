package com.example.ioproject.controllers;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.security.services.ReservationService;
import com.example.ioproject.security.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // Endpoint: Pobierz listę rezerwacji (dla admina i moderatora)
    @GetMapping("/get")
    // @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // Endpoint: Pobierz rezerwacje po ID (dla admina)
    @GetMapping("/get/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public Optional<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    // Endpoint: Dodaj nową rezerwację (dla admina i ??klienta??)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // or hasRole('USER')")
    public ResponseEntity<?> addReservation(@RequestBody Reservation reservation) {
        boolean available = reservationService.isVehicleAvailable(
                reservation.getVehicle_id(),
                reservation.getStart_date(),
                reservation.getEnd_date()
        );

        if (!available) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Rezerwacja nieudana: pojazd jest już zarezerwowany w tym terminie.");
        }

        Reservation saved = reservationService.saveReservation(reservation);
        return ResponseEntity.ok(saved);
    }

    // Endpoint: Zaktualizuj rezerwacje (dla admina)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        reservation.setId(id); // Ustawiamy ID, aby dokonać aktualizacji istniejącej rezerwacji
        return reservationService.saveReservation(reservation);
    }

    // Endpoint: Usuń rezerwacje (dla admina)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }
}