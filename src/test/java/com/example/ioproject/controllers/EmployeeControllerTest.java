package com.example.ioproject.controllers;

import com.example.ioproject.models.User;
import com.example.ioproject.models.UserWithRole;
import com.example.ioproject.repository.RoleRepository;
import com.example.ioproject.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

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
}
