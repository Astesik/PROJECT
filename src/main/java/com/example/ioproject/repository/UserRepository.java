package com.example.ioproject.repository;

import com.example.ioproject.models.User;
import com.example.ioproject.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 *
 * Extends JpaRepository to provide CRUD operations, finding, filtering, and sorting for User objects.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
