package com.umanbeing.umg.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/** Service for handling JSON Web Token (JWT) operations. */
@Component
public class JwtService {

  @Value("${JWT_SECRET}")
  private String secret;

  private final String SUBJECT_JWT = "User Details";

  /**
   * Generates a JWT token for the given username.
   *
   * @param username The username for which the token is generated.
   * @return The generated JWT token.
   * @throws IllegalArgumentException if the username is null or empty.
   * @throws JWTCreationException if token creation fails.
   */
  public String generateToken(String username)
      throws IllegalArgumentException, JWTCreationException {
    return JWT.create()
        .withSubject(SUBJECT_JWT)
        .withClaim("username", username)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1-hour expiration
        .sign(Algorithm.HMAC256(secret));
  }

  /**
   * Validates the JWT token and retrieves the subject (username).
   *
   * <p>This method ensures the token is valid and not expired, then extracts the username claim.
   *
   * @param token The JWT token to validate.
   * @return The username extracted from the token.
   * @throws JWTVerificationException if token verification fails.
   */
  public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
    JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).withSubject(SUBJECT_JWT).build();
    DecodedJWT jwt = verifier.verify(token);
    return jwt.getClaim("username").asString();
  }

  /**
   * Refreshes the JWT token by generating a new one for the same user. It calls {@link
   * #validateTokenAndRetrieveSubject(String)} to validate the token and extract the username from
   * the token.
   *
   * @param token The JWT token to refresh.
   * @return The refreshed JWT token.
   * @throws JWTVerificationException if token verification fails.
   * @throws IllegalArgumentException if the token is null or empty.
   * @throws JWTCreationException if token creation fails.
   */
  public String refreshToken(String token)
      throws JWTVerificationException, IllegalArgumentException, JWTCreationException {
    String username = validateTokenAndRetrieveSubject(token);
    return generateToken(username);
  }

  /**
   * Checks if the JWT token has expired.
   *
   * @param token The JWT token to check.
   * @return true if the token is expired, false otherwise.
   * @throws JWTVerificationException if token verification fails.
   */
  public boolean isTokenExpired(String token) throws JWTVerificationException {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Extracts the expiration date from the JWT token.
   *
   * @param token The JWT token to extract expiration from.
   * @return The expiration date of the token.
   * @throws JWTVerificationException if token verification fails.
   */
  protected Date extractExpiration(String token) throws JWTVerificationException {
    JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).withSubject(SUBJECT_JWT).build();
    DecodedJWT jwt = verifier.verify(token);
    return jwt.getExpiresAt();
  }

  /**
   * Checks if the token is valid.
   *
   * @param token The JWT token to validate.
   * @return true if the token is valid, false otherwise.
   */
  public boolean validateToken(String token) {
    try {
      validateTokenAndRetrieveSubject(token);
      return true;
    } catch (JWTVerificationException e) {
      return false;
    }
  }
}
