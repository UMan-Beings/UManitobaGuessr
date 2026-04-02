package com.umanbeing.umg.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object for creating a new user account. It is used for transferring user account
 * creation data between the controller and service layers.
 *
 * <p>It must contain:
 * <li>a valid username
 * <li>a valid email address
 * <li>a valid password
 */
public record CreateAccountRequest(
    @NotBlank String username, @NotBlank @Email String email, @NotBlank String password) {}
