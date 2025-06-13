package com.example.ioproject.payload.response;

import java.util.List;

/**
 * Response payload returned after successful authentication.
 * <p>
 * Contains the JWT token and basic user information such as ID, username, email, and assigned roles.
 * Sent in response to login or Google OAuth endpoints.
 * </p>
 */
public class JwtResponse {

  /**
   * The JWT access token issued to the client.
   */
  private String token;

  /**
   * The type of the token, usually "Bearer".
   */
  private String type = "Bearer";

  /**
   * The unique ID of the authenticated user.
   */
  private Long id;

  /**
   * The username of the authenticated user.
   */
  private String username;

  /**
   * The email of the authenticated user.
   */
  private String email;

  /**
   * The list of roles assigned to the user (e.g., "ROLE_USER", "ROLE_ADMIN").
   */
  private List<String> roles;

  /**
   * Constructs a full {@link JwtResponse} with all fields set.
   *
   * @param accessToken the JWT access token
   * @param id the user ID
   * @param username the username
   * @param email the email address
   * @param roles the list of roles assigned to the user
   */
  public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }

  /**
   * Gets the JWT access token.
   *
   * @return the token
   */
  public String getAccessToken() {
    return token;
  }

  /**
   * Sets the JWT access token.
   *
   * @param accessToken the token string
   */
  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  /**
   * Gets the type of the token.
   *
   * @return the token type (typically "Bearer")
   */
  public String getTokenType() {
    return type;
  }

  /**
   * Sets the token type.
   *
   * @param tokenType the token type
   */
  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  /**
   * Gets the user's ID.
   *
   * @return the user ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the user's ID.
   *
   * @param id the user ID
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the user's email address.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the user's email address.
   *
   * @param email the email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the user's username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the user's username.
   *
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the list of roles assigned to the user.
   *
   * @return a list of role names
   */
  public List<String> getRoles() {
    return roles;
  }
}
