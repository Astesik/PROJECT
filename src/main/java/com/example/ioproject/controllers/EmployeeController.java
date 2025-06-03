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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff")
public class EmployeeController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {return userDetailsService.getAllUsers();}

    @GetMapping("/users-with-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserWithRole> getUsersWithRoles() {return userDetailsService.getUsersWithRoles();}

    @PutMapping("/users/update")
    @PreAuthorize("hasRole('ADMIN')")
    public UserWithRole updateUser(@RequestBody UserWithRole userWithRole) {
        return userDetailsService.updateUser(userWithRole);
    }

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