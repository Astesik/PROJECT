package com.example.ioproject.auth.service;

import com.example.ioproject.auth.dto.response.GoogleResponse;
import com.example.ioproject.auth.dto.response.JwtResponse;
import com.example.ioproject.auth.dto.response.MessageResponse;
import com.example.ioproject.auth.model.ERole;
import com.example.ioproject.auth.model.Role;
import com.example.ioproject.auth.model.User;
import com.example.ioproject.auth.repository.RoleRepository;
import com.example.ioproject.auth.repository.UserRepository;
import com.example.ioproject.auth.security.UserDetails;
import com.example.ioproject.auth.security.jwt.JwtUtils;
import com.example.ioproject.auth.dto.request.ChangePasswordRequest;
import com.example.ioproject.auth.dto.request.GoogleRequest;
import com.example.ioproject.auth.dto.request.LoginRequest;
import com.example.ioproject.auth.dto.request.SignupRequest;
import com.example.ioproject.security.services.PasswordChangeAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private GoogleAuthService googleAuthService;
    @Autowired private PasswordChangeAttemptService passwordChangeAttemptService;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new JwtResponse(jwt); // tylko token i typ
    }

    public void register(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
    }

    public ResponseEntity<?> authWithGoogle(GoogleRequest request) {
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

        UserDetails userDetails = UserDetails.build(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return ResponseEntity.ok(jwtUtils.generateJwtToken(authToken));
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        if (passwordChangeAttemptService.isBlocked(username)) {
            long left = passwordChangeAttemptService.getBlockSeconds(username);
            throw new RuntimeException("Too many failed attempts. Try again in " + left + " seconds.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            passwordChangeAttemptService.recordFailed(username);
            throw new RuntimeException("Current password is incorrect");
        }

        passwordChangeAttemptService.recordSuccess(username);
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}


