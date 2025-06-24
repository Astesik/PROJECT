package com.example.ioproject.controllers;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.models.User;
import com.example.ioproject.payload.dtos.ReservationDTO;
import com.example.ioproject.repository.UserRepository;
import com.example.ioproject.security.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<ReservationDTO> getAllReservations() {
        return reservationService.getAllReservations()
                .stream()
                .map(reservationService::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationService.getReservationById(id);
        return resOpt.map(res -> ResponseEntity.ok(reservationService.toDTO(res)))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long id, @RequestBody ReservationDTO dto) {
        Reservation reservation = reservationService.fromDTO(dto);
        reservation.setId(id);
        Reservation updated = reservationService.saveReservation(reservation);
        return ResponseEntity.ok(reservationService.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addReservation(@RequestBody ReservationDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        boolean available = reservationService.isVehicleAvailable(
                dto.getVehicleId(), dto.getStartDate(), dto.getEndDate()
        );

        if (!available) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Reservation unsuccessful: the vehicle is already booked for this date.");
        }

        Reservation saved = reservationService.createAndSavePendingReservation(
                user.getId(),
                dto.getVehicleId(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getCost()
        );

        return ResponseEntity.ok(reservationService.toDTO(saved));
    }

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
        List<ReservationDTO> dtos = reservations.stream().map(reservationService::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
