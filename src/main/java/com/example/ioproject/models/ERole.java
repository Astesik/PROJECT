package com.example.ioproject.models;

/**
 * Enum representing the different roles available in the system.
 * These roles define access levels and permissions for users.
 */
public enum ERole {
  /** Standard user with basic permissions. */
  ROLE_USER,

  /** User with employee privileges. */
  ROLE_MODERATOR,

  /** Administrator with full system access. */
  ROLE_ADMIN,

  /** Mechanic responsible for vehicle maintenance. */
  ROLE_MECHANIC
}
