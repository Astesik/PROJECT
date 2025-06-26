package com.example.ioproject.controllers;

import com.example.ioproject.models.Reservation;
import com.example.ioproject.security.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    private Reservation sampleReservation;

    @BeforeEach
    void setUp() {
        sampleReservation = new Reservation();
        sampleReservation.setId(1L);
        sampleReservation.setVehicle_id(1);
        sampleReservation.setStart_date(String.valueOf(LocalDate.of(2025, 6, 1)));
        sampleReservation.setEnd_date(String.valueOf(LocalDate.of(2025, 6, 5)));
    }

    @Test
    void testGetAllReservations() {
        when(reservationService.getAllReservations()).thenReturn(List.of(sampleReservation));

        List<Reservation> result = reservationController.getAllReservations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleReservation.getId(), result.get(0).getId());

        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    void testGetReservationById_Found() {
        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(sampleReservation));

        Optional<Reservation> result = reservationController.getReservationById(1L);

        assertTrue(result.isPresent());
        assertEquals(sampleReservation.getId(), result.get().getId());

        verify(reservationService, times(1)).getReservationById(1L);
    }

    @Test
    void testGetReservationById_NotFound() {
        when(reservationService.getReservationById(99L)).thenReturn(Optional.empty());

        Optional<Reservation> result = reservationController.getReservationById(99L);

        assertFalse(result.isPresent());

        verify(reservationService, times(1)).getReservationById(99L);
    }


    @Test
    void testUpdateReservation() {
        Reservation updatedReservation = new Reservation();
        updatedReservation.setId(1L);
        updatedReservation.setVehicle_id(1);
        updatedReservation.setStart_date(String.valueOf(LocalDate.of(2025, 6, 10)));
        updatedReservation.setEnd_date(String.valueOf(LocalDate.of(2025, 6, 15)));

        when(reservationService.saveReservation(updatedReservation)).thenReturn(updatedReservation);

        Reservation result = reservationController.updateReservation(1L, updatedReservation);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getVehicle_id());

        verify(reservationService, times(1)).saveReservation(updatedReservation);
    }

    @Test
    void testDeleteReservation() {
        doNothing().when(reservationService).deleteReservation(1L);

        reservationController.deleteReservation(1L);

        verify(reservationService, times(1)).deleteReservation(1L);
    }
}
