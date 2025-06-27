package com.example.ioproject.auth.controller;

import com.example.ioproject.auth.dto.response.JwtResponse;
import com.example.ioproject.auth.service.AuthService;
import com.example.ioproject.auth.dto.request.ChangePasswordRequest;
import com.example.ioproject.auth.dto.request.GoogleRequest;
import com.example.ioproject.auth.dto.request.LoginRequest;
import com.example.ioproject.auth.dto.request.SignupRequest;
import com.example.ioproject.auth.dto.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for handling authentication-related endpoints.
 * <p>
 * Supports login, registration, Google OAuth authentication, password change,
 * and retrieval of current authenticated user details.
 * </p>
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
    JwtResponse jwtResponse = authService.login(request);
    return ResponseEntity.ok(jwtResponse);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> register(@Valid @RequestBody SignupRequest request) {
    authService.register(request);
    return ResponseEntity.ok(new MessageResponse("Registered successfully"));
  }

  @PostMapping("/google")
  public ResponseEntity<?> google(@Valid @RequestBody GoogleRequest request) {
      return authService.authWithGoogle(request);
  }

  @PostMapping("/change-password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    authService.changePassword(username, request);
    return ResponseEntity.ok(new MessageResponse("Password changed"));
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getMe() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(authService.getCurrentUser(username));
  }
}

