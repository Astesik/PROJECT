package com.example.ioproject.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request payload used for user authentication.
 * <p>
 * Sent from the frontend when a user attempts to log in.
 * Contains username and password credentials.
 * </p>
 */
@Getter
@Setter
public class LoginRequest {

  /**
   * The username of the user attempting to log in.
   * Cannot be blank.
   */
  @NotBlank
  private String username;

  /**
   * The password of the user attempting to log in.
   * Cannot be blank.
   */
  @NotBlank
  private String password;
}
