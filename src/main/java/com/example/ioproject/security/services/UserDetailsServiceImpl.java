package com.example.ioproject.security.services;

import com.example.ioproject.models.ERole;
import com.example.ioproject.models.Role;
import com.example.ioproject.models.User;
import com.example.ioproject.models.UserWithRole;
import com.example.ioproject.repository.RoleRepository;
import com.example.ioproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for retrieving user-related data from the database.
 * <p>
 * Implements {@link UserDetailsService} for Spring Security authentication.
 * Also provides utility methods for managing users and their roles.
 * </p>
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  /**
   * Loads user-specific data by username for authentication.
   *
   * @param username the username to search for
   * @return a {@link UserDetails} object containing user credentials and authorities
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }

  public List<User> getAllUsers(){return userRepository.findAll();}

  /**
   * Retrieves a list of users along with their roles.
   *
   * @return a list of {@link UserWithRole} objects
   */
  public List<UserWithRole> getUsersWithRoles() {
    List<User> users = userRepository.findAll();

    return users.stream().map(user -> {
      String roleName = user.getRoles()
              .stream()
              .findFirst()
              .map(role -> role.getName().name())
              .orElse("NO_ROLE");

      return new UserWithRole(
              user.getId(),
              user.getUsername(),
              user.getEmail(),
              roleName
      );
    }).collect(Collectors.toList());
  }

  public void deleteUser(Long id) {userRepository.deleteById(id);}

  /**
   * Updates the user's username, email, and role.
   *
   * @param userWithRole a {@link UserWithRole} containing the updated user information
   * @return the updated {@link UserWithRole}
   * @throws RuntimeException if the user or role is not found
   */
  public UserWithRole updateUser(UserWithRole userWithRole) {

    User user = userRepository.findById(userWithRole.getId())
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userWithRole.getId()));

    user.setUsername(userWithRole.getUsername());
    user.setEmail(userWithRole.getEmail());

    Role newRole = roleRepository.findByName(ERole.valueOf(userWithRole.getRole()))
            .orElseThrow(() -> new RuntimeException("Role not found: " + userWithRole.getRole()));

    user.getRoles().clear();
    user.getRoles().add(newRole);

    userRepository.save(user);

    return new UserWithRole(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            newRole.getName().name()
    );
  }
}
