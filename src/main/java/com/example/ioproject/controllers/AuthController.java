package com.example.ioproject.controllers;

import com.example.ioproject.models.ERole;
import com.example.ioproject.models.Role;
import com.example.ioproject.models.User;
import com.example.ioproject.payload.request.ChangePasswordRequest;
import com.example.ioproject.payload.request.GoogleRequest;
import com.example.ioproject.payload.request.LoginRequest;
import com.example.ioproject.payload.request.SignupRequest;
import com.example.ioproject.payload.response.GoogleResponse;
import com.example.ioproject.payload.response.JwtResponse;
import com.example.ioproject.payload.response.MessageResponse;
import com.example.ioproject.repository.RoleRepository;
import com.example.ioproject.repository.UserRepository;
import com.example.ioproject.security.jwt.JwtUtils;
import com.example.ioproject.security.services.GoogleAuthService;
import com.example.ioproject.security.services.PasswordChangeAttemptService;
import com.example.ioproject.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  private PasswordChangeAttemptService passwordChangeAttemptService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  GoogleAuthService googleAuthService;

  /**
   * Authenticates a user using username and password credentials.
   * If authentication is successful, returns a JWT token and user info.
   *
   * @param loginRequest the request body containing login credentials
   * @return {@link JwtResponse} with user info and access token
   */
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
            .collect(Collectors.toList());

    return ResponseEntity
            .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  /**
   * Registers a new user account using the provided signup request.
   *
   * @param signUpRequest the request body with registration data
   * @return a {@link MessageResponse} indicating result
   */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(userRole);

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  /**
   * Authenticates or registers a user using Google Sign-In.
   * Verifies Google ID token and creates an account if needed.
   *
   * @param request contains the Google ID token and client info
   * @return {@link JwtResponse} if successful, or error response
   */
  @PostMapping("/google")
  public ResponseEntity<?> authWithGoogle(@Valid @RequestBody GoogleRequest request) {
    String IdToken = googleAuthService.retriveIdToken(request);

    var payload = googleAuthService.verify(IdToken);
    if (payload == null) {
      return ResponseEntity.badRequest().body(new MessageResponse("Invalid Google ID token."));
    }

    String email = (String) payload.get("email");
    String name = (String) payload.get("name");
    boolean emailVerified = Boolean.TRUE.equals(payload.get("email_verified"));

    if (!emailVerified) {
      return ResponseEntity.badRequest().body(new MessageResponse("Email is not verified by Google."));
    }

    User user = userRepository.findByEmail(email).orElse(null);

    if (user == null) {
      GoogleResponse googleResponse = new GoogleResponse(name, email, false);
      return new ResponseEntity<>(googleResponse, HttpStatus.OK);
    }

    UserDetailsImpl userDetails = UserDetailsImpl.build(user);
    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);
    String jwt = jwtUtils.generateJwtToken(authToken);

    List<String> roles = userDetails.getAuthorities().stream()
            .map(r -> r.getAuthority())
            .collect(Collectors.toList());

    return ResponseEntity
            .ok(new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles));
  }

  /**
   * Returns the currently authenticated user’s information.
   *
   * @return the current user or 404 if not found
   */
  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User user = userRepository.findByUsername(username)
            .orElse(null);
    if (user == null) {
      return ResponseEntity.status(404).body("User not found");
    }

    user.setPassword(null); // Avoid returning password in response
    return ResponseEntity.ok(user);
  }

  /**
   * Allows the authenticated user to change their password,
   * with protection against brute-force attacks.
   *
   * @param request contains old and new password
   * @return success or error message
   */
  @PostMapping("/change-password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    if (passwordChangeAttemptService.isBlocked(username)) {
      long left = passwordChangeAttemptService.getBlockSeconds(username);
      return ResponseEntity.status(429).body(new MessageResponse("Too many failed attempts. Try again in " + left + " seconds."));
    }

    User user = userRepository.findByUsername(username).orElse(null);
    if (user == null) {
      return ResponseEntity.status(404).body(new MessageResponse("User not found"));
    }

    if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
      passwordChangeAttemptService.recordFailed(username);
      return ResponseEntity.badRequest().body(new MessageResponse("Current password is incorrect"));
    }

    passwordChangeAttemptService.recordSuccess(username);
    user.setPassword(encoder.encode(request.getNewPassword()));
    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
  }
}

