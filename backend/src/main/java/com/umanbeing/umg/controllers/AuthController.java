package com.umanbeing.umg.controllers;

import com.umanbeing.umg.controllers.dto.CreateAccountRequest;
import com.umanbeing.umg.controllers.dto.LoginRequest;
import com.umanbeing.umg.controllers.dto.LoginResponse;
import com.umanbeing.umg.controllers.dto.SignUpResponse;
import com.umanbeing.umg.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related endpoints.
 *
 * <p>This controller provides endpoints for user authentication, including login and signup. Each
 * handler validates and processes the request body according to the corresponding DTO. The DTOs are
 * then sent to the AuthService for further processing.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @Autowired private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.loginUser(loginRequest));
  }

  @PostMapping("/signup")
  public ResponseEntity<SignUpResponse> signup(
      @Valid @RequestBody CreateAccountRequest createAccountRequest) {
    return ResponseEntity.ok(authService.registerUser(createAccountRequest));
  }
}
