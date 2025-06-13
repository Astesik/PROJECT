package com.example.ioproject.payload.request;

/**
 * Request payload used for handling Google OAuth authentication.
 * <p>
 * Sent by the frontend after successful Google Sign-In to exchange
 * an authorization code for a token and user identity.
 * </p>
 */
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

    /**
     * Gets the authorization code received from Google.
     *
     * @return the authorization code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the authorization code received from Google.
     *
     * @param code the authorization code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the scope of access granted by Google OAuth.
     *
     * @return the scope string
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope of access granted by Google OAuth.
     *
     * @param scope the OAuth2 scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Gets the authenticated user index (for multiple accounts).
     *
     * @return the authuser value
     */
    public String getAuthuser() {
        return authuser;
    }

    /**
     * Sets the authenticated user index.
     *
     * @param authuser the authuser index
     */
    public void setAuthuser(String authuser) {
        this.authuser = authuser;
    }

    /**
     * Gets the prompt behavior (e.g., "select_account", "consent").
     *
     * @return the prompt parameter
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Sets the prompt behavior for the authorization request.
     *
     * @param prompt the prompt value
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
