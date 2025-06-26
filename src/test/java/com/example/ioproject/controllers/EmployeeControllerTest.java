package com.example.ioproject.controllers;

import com.example.ioproject.auth.model.User;
import com.example.ioproject.auth.dto.UserWithRole;
import com.example.ioproject.auth.repository.RoleRepository;
import com.example.ioproject.auth.security.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RoleRepository roleRepository;

    private User sampleUser;
    private UserWithRole sampleUserWithRole;

    @BeforeEach
    void setUp() {
        sampleUser = new User("testuser", "test@example.com", "password");
        sampleUser.setId(1L);

        sampleUserWithRole = new UserWithRole(1L, "testuser", "test@example.com", "ROLE_USER");
    }

    @Test
    void testGetAllUsers() {
        when(userDetailsService.getAllUsers()).thenReturn(List.of(sampleUser));

        List<User> result = employeeController.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());

        verify(userDetailsService, times(1)).getAllUsers();
    }

    @Test
    void testGetUsersWithRoles() {
        when(userDetailsService.getUsersWithRoles()).thenReturn(List.of(sampleUserWithRole));

        List<UserWithRole> result = employeeController.getUsersWithRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertTrue(result.get(0).getRole().contains("ROLE_USER"));

        verify(userDetailsService, times(1)).getUsersWithRoles();
    }

    @Test
    void testUpdateUser() {
        when(userDetailsService.updateUser(any(UserWithRole.class))).thenReturn(sampleUserWithRole);

        UserWithRole result = employeeController.updateUser(sampleUserWithRole);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userDetailsService, times(1)).updateUser(any(UserWithRole.class));
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userDetailsService).deleteUser(1L);

        ResponseEntity<?> response = employeeController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("User deleted successfully", body.get("message"));

        verify(userDetailsService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUser_Exception() {
        doThrow(new RuntimeException("Deletion failed")).when(userDetailsService).deleteUser(1L);

        ResponseEntity<?> response = employeeController.deleteUser(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Failed to delete user", body.get("error"));

        verify(userDetailsService, times(1)).deleteUser(1L);
    }

    @Test
    void testGetUsersWithRoles_ContainsExpectedRole() {
        UserWithRole userWithRole = new UserWithRole(1L, "admin", "admin@example.com", "ROLE_ADMIN");

        when(userDetailsService.getUsersWithRoles()).thenReturn(List.of(userWithRole));

        List<UserWithRole> result = employeeController.getUsersWithRoles();

        assertEquals("ROLE_ADMIN", result.get(0).getRole());
        verify(userDetailsService, times(1)).getUsersWithRoles();
    }

    @Test
    void testUpdateUser_WithInvalidData() {
        UserWithRole invalidUser = new UserWithRole(2L, "", "invalid@example.com", "ROLE_USER");

        when(userDetailsService.updateUser(any(UserWithRole.class))).thenReturn(invalidUser);

        UserWithRole result = employeeController.updateUser(invalidUser);

        assertEquals("", result.getUsername()); // lub zakładany efekt walidacji
        verify(userDetailsService, times(1)).updateUser(any(UserWithRole.class));
    }

    @Test
    void testUpdateUser_ThrowsException() {
        when(userDetailsService.updateUser(any(UserWithRole.class)))
                .thenThrow(new RuntimeException("Update failed"));

        assertThrows(RuntimeException.class, () -> {
            employeeController.updateUser(sampleUserWithRole);
        });

        verify(userDetailsService, times(1)).updateUser(any(UserWithRole.class));
    }

    @Test
    void testGetUsersWithRoles_EmptyList() {
        when(userDetailsService.getUsersWithRoles()).thenReturn(Collections.emptyList());

        List<UserWithRole> result = employeeController.getUsersWithRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userDetailsService, times(1)).getUsersWithRoles();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        when(userDetailsService.getAllUsers()).thenReturn(Collections.emptyList());

        List<User> result = employeeController.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userDetailsService, times(1)).getAllUsers();
    }
}
