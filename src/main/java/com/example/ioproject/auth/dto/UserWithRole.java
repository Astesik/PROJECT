package com.example.ioproject.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a simplified user object including role information.
 * Used for transferring user data along with their assigned role.
 */
@AllArgsConstructor
@Getter
@Setter
public class UserWithRole {

    private Long id;
    private String username;
    private String email;
    private String role;

}
