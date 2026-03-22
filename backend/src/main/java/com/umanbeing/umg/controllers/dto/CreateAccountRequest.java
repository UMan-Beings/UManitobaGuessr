package com.umanbeing.umg.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(

    @NotBlank
    String username,

    @NotBlank
    @Email
    String email,
    
    @NotBlank
    String password
) {
}
