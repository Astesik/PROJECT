package com.example.ioproject.controllers;

import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.models.Vehicle;
import com.example.ioproject.security.services.MaintenanceService;
import com.example.ioproject.security.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class VehicleControllerTest {

    @InjectMocks
    private VehicleController vehicleController;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private MaintenanceService maintenanceService;

    private Vehicle sampleVehicle;
    private MaintenanceTask sampleMaintenanceTask;

    @BeforeEach
    void setUp() {
        sampleVehicle = new Vehicle();
        sampleVehicle.setId(1L);
        sampleVehicle.setMake("Toyota");
        sampleVehicle.setModel("Corolla");
        sampleVehicle.setProduction_year(2020);
        sampleVehicle.setLicense_plate("XYZ 1234");
        sampleVehicle.setEngine_type("Diesel");
        sampleVehicle.setVehicle_type("SUV");
        sampleVehicle.setMileage(50000);
        sampleVehicle.setStatus("Available");
        sampleVehicle.setImage_url("http://example.com/image.jpg");
        sampleVehicle.setDescription("Test vehicle");
        sampleVehicle.setDaily_rate(100);
        sampleVehicle.setWeekly_rate(500);
        sampleVehicle.setMonthly_rate(1500);
        sampleVehicle.setFeatures(List.of("GPS", "Air Conditioning"));

        sampleMaintenanceTask = new MaintenanceTask();
        sampleMaintenanceTask.setId(1L);
        sampleMaintenanceTask.setDescription("Oil change");
    }


    @Test
    void testGetAllVehicles() {
        when(vehicleService.getAllVehicles()).thenReturn(List.of(sampleVehicle));

        List<Vehicle> result = vehicleController.getAllVehicles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleVehicle.getId(), result.get(0).getId());

        verify(vehicleService, times(1)).getAllVehicles();
    }

    @Test
    void testGetVehicleById_Found() {
        when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(sampleVehicle));

        Optional<Vehicle> result = vehicleController.getVehicleById(1L);

        assertTrue(result.isPresent());
        assertEquals(sampleVehicle.getId(), result.get().getId());

        verify(vehicleService, times(1)).getVehicleById(1L);
    }

    @Test
    void testGetVehicleById_NotFound() {
        when(vehicleService.getVehicleById(99L)).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleController.getVehicleById(99L);

        assertFalse(result.isPresent());

        verify(vehicleService, times(1)).getVehicleById(99L);
    }

    @Test
    void testAddVehicle() {
        when(vehicleService.saveVehicle(sampleVehicle)).thenReturn(sampleVehicle);

        ResponseEntity<Vehicle> response = vehicleController.addVehicle(sampleVehicle);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleVehicle, response.getBody());

        verify(vehicleService, times(1)).saveVehicle(sampleVehicle);
    }

    @Test
    void testUpdateVehicle_Found() {
        when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(vehicleService.saveVehicle(sampleVehicle)).thenReturn(sampleVehicle);

        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(1L, sampleVehicle);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleVehicle, response.getBody());

        verify(vehicleService, times(1)).getVehicleById(1L);
        verify(vehicleService, times(1)).saveVehicle(sampleVehicle);
    }

    @Test
    void testUpdateVehicle_NotFound() {
        when(vehicleService.getVehicleById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(99L, sampleVehicle);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(vehicleService, times(1)).getVehicleById(99L);
        verify(vehicleService, never()).saveVehicle(any());
    }

    @Test
    void testDeleteVehicle_Success() {
        doNothing().when(vehicleService).deleteVehicle(1L);

        ResponseEntity<HttpStatus> response = vehicleController.deleteVehicle(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(vehicleService, times(1)).deleteVehicle(1L);
    }

    @Test
    void testDeleteVehicle_Failure() {
        doThrow(new RuntimeException("Database error")).when(vehicleService).deleteVehicle(1L);

        ResponseEntity<HttpStatus> response = vehicleController.deleteVehicle(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(vehicleService, times(1)).deleteVehicle(1L);
    }

    @Test
    void testCreateMaintenanceTask() {
        when(maintenanceService.saveMaintenance(sampleMaintenanceTask)).thenReturn(sampleMaintenanceTask);

        ResponseEntity<MaintenanceTask> response = vehicleController.createMaintenanceTask(sampleMaintenanceTask);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleMaintenanceTask, response.getBody());

        verify(maintenanceService, times(1)).saveMaintenance(sampleMaintenanceTask);
    }

    @Test
    void testGetMaintenanceTasks() {
        when(maintenanceService.getAllMaintenanceTasks()).thenReturn(List.of(sampleMaintenanceTask));

        List<MaintenanceTask> result = vehicleController.getMaintenanceTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleMaintenanceTask.getId(), result.get(0).getId());

        verify(maintenanceService, times(1)).getAllMaintenanceTasks();
    }

    @Test
    void testUpdateMaintenanceTask_Found() {
        when(maintenanceService.getMaintenanceById(1L)).thenReturn(Optional.of(sampleMaintenanceTask));
        when(maintenanceService.saveMaintenance(sampleMaintenanceTask)).thenReturn(sampleMaintenanceTask);

        ResponseEntity<MaintenanceTask> response = vehicleController.updateMaintenanceTAsk(1L, sampleMaintenanceTask);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleMaintenanceTask, response.getBody());

        verify(maintenanceService, times(1)).getMaintenanceById(1L);
        verify(maintenanceService, times(1)).saveMaintenance(sampleMaintenanceTask);
    }

    @Test
    void testUpdateMaintenanceTask_NotFound() {
        when(maintenanceService.getMaintenanceById(99L)).thenReturn(Optional.empty());

        ResponseEntity<MaintenanceTask> response = vehicleController.updateMaintenanceTAsk(99L, sampleMaintenanceTask);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(maintenanceService, times(1)).getMaintenanceById(99L);
        verify(maintenanceService, never()).saveMaintenance(any());
    }
}
