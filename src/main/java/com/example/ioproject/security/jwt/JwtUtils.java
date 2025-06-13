package com.example.ioproject.security.jwt;

import com.example.ioproject.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for handling JSON Web Tokens (JWT) operations such as
 * generating, parsing, and validating JWT tokens.
 */
@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${ioproject.app.jwtSecret}")
  private String jwtSecret;

  @Value("${ioproject.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  /**
   * Generates a JWT token for the authenticated user.
   *
   * @param authentication the authentication object containing user details
   * @return the generated JWT token as a String
   */
  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Builds the signing key used for signing and verifying JWT tokens
   * based on the secret key configured in application properties.
   *
   * @return the secret key used for signing JWT tokens
   */
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  /**
   * Extracts the username (subject) from the JWT token.
   *
   * @param token the JWT token
   * @return the username extracted from the token
   */
  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
               .parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Validates the JWT token by parsing it and checking its signature,
   * expiration date, and format.
   *
   * @param authToken the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
