package com.example.ioproject.auth.config;

import com.example.ioproject.auth.security.UserDetailsService;
import com.example.ioproject.auth.security.jwt.AuthEntryPointJwt;
import com.example.ioproject.auth.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 * <p>
 * Configures authentication, password encoding, JWT filter, and security rules for HTTP requests.
 * </p>
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
  @Autowired
  UserDetailsService userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  /**
   * Configures the authentication provider using the custom {@link UserDetailsService}
   * and {@link BCryptPasswordEncoder} for password encoding.
   *
   * @return a configured {@link DaoAuthenticationProvider}
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }

  /**
   * Provides the {@link AuthenticationManager} bean used for authentication processes.
   *
   * @param authConfig the {@link AuthenticationConfiguration}
   * @return an {@link AuthenticationManager} instance
   * @throws Exception if authentication manager cannot be retrieved
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * Defines the {@link PasswordEncoder} bean to be used throughout the application.
   *
   * @return a {@link BCryptPasswordEncoder} instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Defines the main security filter chain, configuring endpoint access rules, session policy,
   * exception handling, and JWT filter insertion.
   *
   * @param http the {@link HttpSecurity} configuration
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
          auth.requestMatchers(
                              "/api/auth/**",
                              "/api/test/**",
                              "/api/vehicles/get/**",
                              "/api/payment/webhook")
                  .permitAll()
                  .anyRequest()
                  .authenticated()
        );
    
    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }
}