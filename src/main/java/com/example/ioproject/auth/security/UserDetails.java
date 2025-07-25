package com.example.ioproject.auth.security;

import com.example.ioproject.auth.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Custom implementation of {@link org.springframework.security.core.userdetails.UserDetails} used by Spring Security to authenticate and authorize users.
 * <p>
 * Wraps the {@link User} entity and converts roles to {@link GrantedAuthority} objects.
 * </p>
 */
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String username;

  private String email;

  @JsonIgnore
  private String password;

  private Collection<? extends GrantedAuthority> authorities;

  /**
   * Constructs a new {@code UserDetailsImpl} object.
   *
   * @param id          the user's ID
   * @param username    the username
   * @param email       the email address
   * @param password    the encrypted password (ignored in JSON output)
   * @param authorities the user's roles as Spring Security authorities
   */
  public UserDetails(Long id, String username, String email, String password,
                     Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  /**
   * Builds a {@code UserDetailsImpl} instance from a {@link User} entity.
   *
   * @param user the {@link User} entity
   * @return a new {@code UserDetailsImpl} object
   */
  public static UserDetails build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
                               .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                               .collect(Collectors.toList());

    return new UserDetails(user.getId(),
                               user.getUsername(), 
                               user.getEmail(),
                               user.getPassword(), 
                               authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Compares this {@code UserDetailsImpl} to another object.
   *
   * @param o the object to compare
   * @return {@code true} if the IDs are equal; {@code false} otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetails user = (UserDetails) o;
    return Objects.equals(id, user.id);
  }
}
