package com.example.ioproject.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload used for handling Google OAuth authentication.
 * <p>
 * Sent by the frontend after successful Google Sign-In to exchange
 * an authorization code for a token and user identity.
 * </p>
 */
@Getter
@Setter
public class GoogleRequest {

    /**
     * Authorization code returned by Google after user login.
     */
    private String code;

    /**
     * OAuth2 scope indicating granted access permissions.
     */
    private String scope;

    /**
     * Index of the authenticated user if multiple accounts are used.
     */
    private String authuser;

    /**
     * Prompt behavior used during the authorization request (e.g., "consent").
     */
    private String prompt;
}
