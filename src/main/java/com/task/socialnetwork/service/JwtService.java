package com.task.socialnetwork.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expiration;

  /**
   * Generates a signing key using the configured secret.
   *
   * @return A Key instance for signing JWTs.
   */
  private Key getSigningKey() {
    byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret); // Decode the Base64 secret
    return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
  }

  /**
   * Generates a JWT token for the given user details.
   *
   * @param userDetails The user's details.
   * @return A signed JWT token.
   */
  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(
            System.currentTimeMillis() + expiration)) // Use expiration value from properties.
        .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign using the generated key.
        .compact();
  }

  /**
   * Extracts the username from a JWT token.
   *
   * @param token The JWT token.
   * @return The username extracted from the token.
   */
  public String extractUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  /**
   * Validates a JWT token.
   *
   * @param token       The token to validate.
   * @param userDetails The user's details for comparison.
   * @return True if the token is valid, false otherwise.
   */
  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * Checks if a token has expired.
   *
   * @param token The token to check.
   * @return True if the token has expired, false otherwise.
   */
  private boolean isTokenExpired(String token) {
    final Date expirationDate = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
    return expirationDate.before(new Date());
  }
}