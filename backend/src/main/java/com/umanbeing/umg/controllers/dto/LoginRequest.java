package com.umanbeing.umg.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object for user login. It is used for transferring user login data between the
 * controller and service layers.
 *
 * <p>It must contain:
 * <li>a valid email address
 * <li>a valid password
 */
public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
