package com.example.ioproject.repository;

import com.example.ioproject.models.ERole;
import com.example.ioproject.models.Role;
import com.example.ioproject.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Role} entities.
 *
 * Extends JpaRepository to provide CRUD operations, finding, and sorting for Role objects.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
