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

/**
 * REST controller for managing vehicle reservations.
 * <p>
 * Provides endpoints for creating, retrieving, updating, and deleting reservations.
 * Includes admin-only operations as well as authenticated user-specific endpoints.
 * </p>
 */
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a list of all reservations in the system.
     * Accessible only to administrators.
     *
     * @return a list of {@link Reservation} objects
     */
    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    /**
     * Retrieves a reservation by its ID.
     * Accessible only to administrators.
     *
     * @param id the reservation ID
     * @return an {@link Optional} containing the reservation, if found
     */
    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    /**
     * Updates an existing reservation.
     * Only administrators can perform this operation.
     *
     * @param id          the ID of the reservation to update
     * @param reservation the new reservation data
     * @return the updated {@link Reservation} object
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        reservation.setId(id);
        return reservationService.saveReservation(reservation);
    }

    /**
     * Deletes a reservation by its ID.
     * Only accessible to administrators.
     *
     * @param id the ID of the reservation to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    /**
     * Creates a new reservation for the currently authenticated user.
     * Automatically assigns the user as the reservation owner.
     * Validates vehicle availability for the requested period.
     *
     * @param reservation the reservation data
     * @return the created {@link Reservation} or error if the vehicle is already reserved
     */
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

    /**
     * Marks a reservation as paid manually.
     * Can be used by administrators when payment is completed outside Stripe.
     *
     * @param id the ID of the reservation to mark as paid
     * @return a success message
     */
    @PutMapping("/{id}/pay-manual")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markReservationAsManual(@PathVariable Long id) {
        reservationService.markAsManual(id);
        return ResponseEntity.ok("Reservation marked as manually paid");
    }

    /**
     * Retrieves all reservations made by the currently authenticated user.
     *
     * @return a list of the user's own reservations
     */
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
