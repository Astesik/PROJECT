package com.example.ioproject.repository;

import com.example.ioproject.models.Vehicle;
import com.example.ioproject.security.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
public class VehicleRepositoryTest {
    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        sampleVehicle = new Vehicle();
        sampleVehicle.setId(1L);
        sampleVehicle.setModel("Model X");
        sampleVehicle.setMake("BrandY");
    }

    @Test
    void testGetAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(sampleVehicle));

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Model X", result.get(0).getModel());

        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void testGetVehicleById_Found() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleById(1L);

        assertTrue(result.isPresent());
        assertEquals("BrandY", result.get().getMake());

        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    void testGetVehicleById_NotFound() {
        when(vehicleRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleService.getVehicleById(2L);

        assertFalse(result.isPresent());

        verify(vehicleRepository, times(1)).findById(2L);
    }

    @Test
    void testSaveVehicle_WithIdZero_SetsIdNull() {
        Vehicle vehicleWithIdZero = new Vehicle();
        vehicleWithIdZero.setId(0L);
        vehicleWithIdZero.setModel("Model Zero");

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(10L);
        savedVehicle.setModel("Model Zero");

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        Vehicle result = vehicleService.saveVehicle(vehicleWithIdZero);

        assertNotNull(result);
        assertEquals(10L, result.getId());

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(captor.capture());

        assertNull(captor.getValue().getId()); // sprawdzamy, czy przed zapisem id zostało ustawione na null
    }

    @Test
    void testSaveVehicle_Normal() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        Vehicle result = vehicleService.saveVehicle(sampleVehicle);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(vehicleRepository, times(1)).save(sampleVehicle);
    }

    @Test
    void testDeleteVehicle() {
        doNothing().when(vehicleRepository).deleteById(1L);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository, times(1)).deleteById(1L);
    }
}
