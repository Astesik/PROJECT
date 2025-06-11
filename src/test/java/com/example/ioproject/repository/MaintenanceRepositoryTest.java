package com.example.ioproject.repository;
import com.example.ioproject.models.MaintenanceTask;
import com.example.ioproject.security.services.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaintenanceRepositoryTest {
    @InjectMocks
    private MaintenanceService maintenanceService;

    @Mock
    private MaintenanceRepository maintenanceRepository;

    private MaintenanceTask sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new MaintenanceTask();
        sampleTask.setId(1L);
    }

    @Test
    void testGetAllMaintenanceTasks() {
        when(maintenanceRepository.findAll()).thenReturn(List.of(sampleTask));

        List<MaintenanceTask> result = maintenanceService.getAllMaintenanceTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(maintenanceRepository, times(1)).findAll();
    }

    @Test
    void testGetMaintenanceById_Found() {
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Optional<MaintenanceTask> result = maintenanceService.getMaintenanceById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());

        verify(maintenanceRepository, times(1)).findById(1L);
    }

    @Test
    void testGetMaintenanceById_NotFound() {
        when(maintenanceRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<MaintenanceTask> result = maintenanceService.getMaintenanceById(2L);

        assertFalse(result.isPresent());

        verify(maintenanceRepository, times(1)).findById(2L);
    }

    @Test
    void testSaveMaintenance_WithIdZero_SetsIdNull() {
        MaintenanceTask taskWithIdZero = new MaintenanceTask();
        taskWithIdZero.setId(0L);

        MaintenanceTask savedTask = new MaintenanceTask();
        savedTask.setId(10L);

        when(maintenanceRepository.save(any(MaintenanceTask.class))).thenReturn(savedTask);

        MaintenanceTask result = maintenanceService.saveMaintenance(taskWithIdZero);

        assertNotNull(result);
        assertEquals(10L, result.getId());

        ArgumentCaptor<MaintenanceTask> captor = ArgumentCaptor.forClass(MaintenanceTask.class);
        verify(maintenanceRepository).save(captor.capture());

        assertNull(captor.getValue().getId());
    }

    @Test
    void testSaveMaintenance_Normal() {
        when(maintenanceRepository.save(any(MaintenanceTask.class))).thenReturn(sampleTask);

        MaintenanceTask result = maintenanceService.saveMaintenance(sampleTask);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(maintenanceRepository, times(1)).save(sampleTask);
    }

    @Test
    void testDeleteMaintenanceTask() {
        doNothing().when(maintenanceRepository).deleteById(1L);

        maintenanceService.deleteMaintenanceTask(1L);

        verify(maintenanceRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMaintenanceTask_ThrowsException() {
        doThrow(new RuntimeException("DB error")).when(maintenanceRepository).deleteById(999L);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            maintenanceService.deleteMaintenanceTask(999L);
        });

        assertEquals("DB error", thrown.getMessage());
        verify(maintenanceRepository, times(1)).deleteById(999L);
    }

    @Test
    void testSaveMaintenance_ThrowsException() {
        when(maintenanceRepository.save(any(MaintenanceTask.class)))
                .thenThrow(new RuntimeException("Save error"));

        MaintenanceTask task = new MaintenanceTask();
        task.setId(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            maintenanceService.saveMaintenance(task);
        });

        assertEquals("Save error", thrown.getMessage());
        verify(maintenanceRepository, times(1)).save(task);
    }

    @Test
    void testGetAllMaintenanceTasks_LargeList() {
        List<MaintenanceTask> largeList = new ArrayList<>();
        for (long i = 0; i < 1000; i++) {
            MaintenanceTask task = new MaintenanceTask();
            task.setId(i);
            largeList.add(task);
        }

        when(maintenanceRepository.findAll()).thenReturn(largeList);

        List<MaintenanceTask> result = maintenanceService.getAllMaintenanceTasks();

        assertNotNull(result);
        assertEquals(1000, result.size());
        assertEquals(0L, result.get(0).getId());
        assertEquals(999L, result.get(999).getId());

        verify(maintenanceRepository, times(1)).findAll();
    }

    @Test
    void testSaveMaintenance_IdNotZero_DoesNotChangeId() {
        MaintenanceTask taskWithIdFive = new MaintenanceTask();
        taskWithIdFive.setId(5L);

        when(maintenanceRepository.save(any(MaintenanceTask.class))).thenReturn(taskWithIdFive);

        MaintenanceTask result = maintenanceService.saveMaintenance(taskWithIdFive);

        assertNotNull(result);
        assertEquals(5L, result.getId());

        ArgumentCaptor<MaintenanceTask> captor = ArgumentCaptor.forClass(MaintenanceTask.class);
        verify(maintenanceRepository).save(captor.capture());

        assertEquals(5L, captor.getValue().getId()); // id nie zostało zmienione
    }


}
