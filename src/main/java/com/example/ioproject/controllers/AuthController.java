package com.example.ioproject.controllers;

import com.example.ioproject.models.ERole;
import com.example.ioproject.models.Role;
import com.example.ioproject.models.User;
import com.example.ioproject.payload.request.GoogleRequest;
import com.example.ioproject.payload.request.LoginRequest;
import com.example.ioproject.payload.request.SignupRequest;
import com.example.ioproject.payload.response.JwtResponse;
import com.example.ioproject.payload.response.MessageResponse;
import com.example.ioproject.repository.RoleRepository;
import com.example.ioproject.repository.UserRepository;
import com.example.ioproject.security.jwt.JwtUtils;
import com.example.ioproject.security.services.GoogleAuthService;
import com.example.ioproject.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

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

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
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

  @PostMapping("/google")
  public ResponseEntity<?> authWithGoogle(@Valid @RequestBody GoogleRequest request){
    var payload = googleAuthService.verify(request.getIdToken());
    if (payload == null) {
      return ResponseEntity.badRequest().body(new MessageResponse("Invalid Google ID token."));
    }

    String email = (String) payload.get("email");
    String name = (String) payload.get("name");
    boolean emailVerified = Boolean.TRUE.equals(payload.get("email_verified"));

    if (!emailVerified) {
      return ResponseEntity.badRequest().body(new MessageResponse("Email is not verified by Google."));
    }

    System.out.println(payload);

    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null) {
      String randomPassword = UUID.randomUUID().toString();  // wygeneruj randomowe haslo dla uzytkownika autoryzujacego sie przez google
      user = new User(name, email, randomPassword);
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      user.setRoles(Set.of(userRole));
      userRepository.save(user);
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
}
