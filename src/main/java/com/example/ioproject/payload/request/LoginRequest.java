package com.example.ioproject.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload used for user authentication.
 * <p>
 * Sent from the frontend when a user attempts to log in.
 * Contains username and password credentials.
 * </p>
 */
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

  /**
   * Gets the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username.
   *
   * @param username the user's login name
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the user's password
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
