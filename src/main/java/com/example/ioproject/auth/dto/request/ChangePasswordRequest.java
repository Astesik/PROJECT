package com.example.ioproject.auth.dto.request;

/**
 * Request payload used for changing a user's password.
 * <p>
 * Contains the user's current password and the desired new password.
 * Used in the {@code /api/auth/change-password} endpoint.
 * </p>
 */
public class ChangePasswordRequest {

    /**
     * The current password of the user.
     */
    private String oldPassword;

    /**
     * The new password the user wants to set.
     */
    private String newPassword;

    /**
     * Gets the user's current password.
     *
     * @return the old password
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Sets the user's current password.
     *
     * @param oldPassword the existing password to verify
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Gets the user's new desired password.
     *
     * @return the new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the user's new desired password.
     *
     * @param newPassword the new password to be saved
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
