package com.example.ioproject.repository;


import com.example.ioproject.models.Reservation;
import com.example.ioproject.repository.ReservationRepository;
import com.example.ioproject.security.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationRepositoryTest {
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllReservations() {
        List<Reservation> reservations = List.of(new Reservation(), new Reservation());
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> result = reservationService.getAllReservations();

        assertEquals(2, result.size());
        verify(reservationRepository).findAll();
    }

    @Test
    void testGetReservationById() {
        Reservation res = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));

        Optional<Reservation> result = reservationService.getReservationById(1L);

        assertTrue(result.isPresent());
        assertEquals(res, result.get());
    }

    @Test
    void testSaveReservation() {
        Reservation res = new Reservation();
        when(reservationRepository.save(res)).thenReturn(res);

        Reservation result = reservationService.saveReservation(res);

        assertEquals(res, result);
        verify(reservationRepository).save(res);
    }

    @Test
    void testDeleteReservation() {
        reservationService.deleteReservation(1L);
        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void testIsVehicleAvailable_ReturnsTrue() {
        when(reservationRepository.findConflictingReservations(1, "2025-06-10", "2025-06-15"))
                .thenReturn(Collections.emptyList());

        boolean result = reservationService.isVehicleAvailable(1, "2025-06-10", "2025-06-15");

        assertTrue(result);
    }

    @Test
    void testIsVehicleAvailable_ReturnsFalse() {
        List<Reservation> conflicts = List.of(new Reservation());
        when(reservationRepository.findConflictingReservations(1, "2025-06-10", "2025-06-15"))
                .thenReturn(conflicts);

        boolean result = reservationService.isVehicleAvailable(1, "2025-06-10", "2025-06-15");

        assertFalse(result);
    }

    @Test
    void testMarkAsPaid() {
        Reservation res = new Reservation();
        res.setStatus("PENDING");
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));

        reservationService.markAsPaid(1L);

        assertEquals("PAID", res.getStatus());
        verify(reservationRepository).save(res);
    }


    @Test
    void testMarkAsManual() {
        Reservation res = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));

        reservationService.markAsManual(1L);

        assertEquals("MANUAL", res.getStatus());
        verify(reservationRepository).save(res);
    }

    @Test
    void markAsManual_shouldNotSaveWhenNotFound() {
        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());

        reservationService.markAsManual(2L);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testMarkAsCancelled() {
        Reservation res = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));

        reservationService.markAsCancelled(1L);

        assertEquals("CANCELLED", res.getStatus());
        verify(reservationRepository).save(res);
    }

    @Test
    void testSetStripeSessionId() {
        Reservation res = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));

        reservationService.setStripeSessionId(1L, "session123");

        assertEquals("session123", res.getStripeSessionId());
        verify(reservationRepository).save(res);
    }

    @Test
    void testFindByStripeSessionId() {
        Reservation res = new Reservation();
        when(reservationRepository.findByStripeSessionId("session123"))
                .thenReturn(Optional.of(res));

        Optional<Reservation> result = reservationService.findByStripeSessionId("session123");

        assertTrue(result.isPresent());
        assertEquals(res, result.get());
    }
}
