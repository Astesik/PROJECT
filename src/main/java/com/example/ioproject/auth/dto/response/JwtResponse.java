package com.example.ioproject.auth.dto.response;

import java.util.List;

/**
 * Response payload returned after successful authentication.
 * <p>
 * Contains the JWT token and claims with basic user information such as ID, username, email, and assigned roles.
 * Sent in response to login or Google OAuth endpoints.
 * </p>
 */
public class JwtResponse {
  private String accessToken;

  public JwtResponse(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}

