package com.example.ioproject.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Request payload used for user registration (sign-up).
 * <p>
 * Contains fields for username, email, password, and optionally a set of roles to assign.
 * Validated with constraints on size and format.
 * </p>
 */
public class SignupRequest {

  /**
   * The username to be registered.
   * Must be between 3 and 20 characters.
   */
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  /**
   * The email address of the user.
   * Must be a valid email format and not exceed 50 characters.
   */
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  /**
   * The roles to be assigned to the user.
   * If null, default roles (e.g., ROLE_USER) may be applied.
   */
  private Set<String> role;

  /**
   * The password for the new account.
   * Must be between 6 and 40 characters.
   */
  @NotBlank
  @Size(min = 6, max = 40)
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
   * @param username the desired login name
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the email.
   *
   * @return the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the user's email address
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the password.
   *
   * @return the plain-text password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the plain-text password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the set of role names to assign.
   *
   * @return a set of role strings
   */
  public Set<String> getRole() {
    return this.role;
  }

  /**
   * Sets the set of roles to assign to the user.
   *
   * @param role the roles as string identifiers (e.g., "admin", "user")
   */
  public void setRole(Set<String> role) {
    this.role = role;
  }
}
