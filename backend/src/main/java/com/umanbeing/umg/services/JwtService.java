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

@Component
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secret;

    private String subjectJWT = "User Details";

    public String generateToken(String username) throws
            IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject(subjectJWT)
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour expiration
                .sign(Algorithm.HMAC256(secret));

    }

    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(subjectJWT)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }

    public String refreshToken(String token) throws JWTVerificationException, IllegalArgumentException, JWTCreationException {
        String username = validateTokenAndRetrieveSubject(token);
        return generateToken(username);
    }

    public boolean isTokenExpired(String token) throws JWTVerificationException {
        return extractExpiration(token).before(new Date());
    }

    protected Date extractExpiration(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(subjectJWT)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getExpiresAt();
    }

    public boolean validateToken(String token) {
        try {
            validateTokenAndRetrieveSubject(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

}
