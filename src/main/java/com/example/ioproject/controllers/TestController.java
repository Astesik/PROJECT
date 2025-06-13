package com.example.ioproject.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller used to verify role-based access control (RBAC).
 * <p>
 * Provides sample endpoints that return simple string content based on the user's role.
 * Useful for testing Spring Security configurations.
 * </p>
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

  /**
   * Publicly accessible endpoint without any authentication.
   *
   * @return a simple public message
   */
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  /**
   * Endpoint accessible to authenticated users with roles: USER, MODERATOR, or ADMIN.
   *
   * @return content for users with general access
   */
  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public String userAccess() {
    return "User Content.";
  }

  /**
   * Endpoint accessible only to users with the MODERATOR role.
   *
   * @return moderator-specific content
   */
  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  /**
   * Endpoint accessible only to users with the ADMIN role.
   *
   * @return admin-specific content
   */
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }
}

