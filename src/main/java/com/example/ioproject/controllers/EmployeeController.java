package com.example.ioproject.controllers;

import com.example.ioproject.models.*;
import com.example.ioproject.payload.response.MessageResponse;
import com.example.ioproject.repository.RoleRepository;
import com.example.ioproject.repository.UserRepository;
import com.example.ioproject.security.services.MaintenanceService;
import com.example.ioproject.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for administrative operations on users.
 * <p>
 * Provides endpoints for managing user accounts, including listing all users,
 * retrieving roles, updating and deleting users. Restricted to ADMIN role.
 * </p>
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff")
public class EmployeeController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    RoleRepository roleRepository;

    /**
     * Retrieves all user accounts in the system.
     * Accessible only by administrators.
     *
     * @return a list of {@link User} objects
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userDetailsService.getAllUsers();
    }

    /**
     * Retrieves all users along with their assigned roles.
     * Useful for displaying user-role assignments in admin panels.
     *
     * @return a list of {@link UserWithRole} objects
     */
    @GetMapping("/users-with-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserWithRole> getUsersWithRoles() {
        return userDetailsService.getUsersWithRoles();
    }

    /**
     * Updates user account information including assigned roles.
     * Requires ADMIN privileges.
     *
     * @param userWithRole the updated user object with new role assignment
     * @return the updated {@link UserWithRole} object
     */
    @PutMapping("/users/update")
    @PreAuthorize("hasRole('ADMIN')")
    public UserWithRole updateUser(@RequestBody UserWithRole userWithRole) {
        return userDetailsService.updateUser(userWithRole);
    }

    /**
     * Deletes a user account by ID. Only administrators are allowed to perform this action.
     *
     * @param id the ID of the user to delete
     * @return a {@link ResponseEntity} indicating success or failure
     */
    @DeleteMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userDetailsService.deleteUser(id);
            return ResponseEntity.ok().body(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user"));
        }
    }
}
