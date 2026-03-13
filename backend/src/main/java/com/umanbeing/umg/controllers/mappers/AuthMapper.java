package com.umanbeing.umg.controllers.mappers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.umanbeing.umg.models.User;

import com.umanbeing.umg.controllers.dto.CreateAccountRequest;
import com.umanbeing.umg.controllers.dto.LoginRequest;
import com.umanbeing.umg.controllers.dto.LoginResponse;

public class AuthMapper {
    
    private AuthMapper() {
    throw new UnsupportedOperationException("This class should never be instantiated");
  }

  public static User fromDto(final CreateAccountRequest createUserDto) {
    return User.builder()
      .email(createUserDto.email())
      .username(createUserDto.username())
      .build();
  }

  public static Authentication fromDto(final LoginRequest loginRequest) {
    return new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
  }

  public static LoginResponse toDto(final User user) {
    return new LoginResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole());
  }

}
