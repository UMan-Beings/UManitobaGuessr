package com.umanbeing.umg.controllers.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;

import com.umanbeing.umg.domain.HttpRes;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpRes<Void>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Access denied to {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpRes<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Method not allowed for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED.value()).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpRes<Void>> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("No handler found for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<HttpRes<Void>> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("No resource found for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<HttpRes<Void>> handleResponseStatusException(ResponseStatusException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Response status exception for {}: {}", requestUri, e.getMessage());
        int statusCodeValue = e.getStatusCode().value();
        HttpStatus httpStatus = HttpStatus.resolve(statusCodeValue);
        if (httpStatus == null) {
            logger.warn("Received non-standard HTTP status code {} for {}: {}", statusCodeValue, requestUri, e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        HttpRes<Void> response = HttpRes.fail(httpStatus, e.getReason());
        return ResponseEntity.status(statusCodeValue).body(response);
    }

    // Spring security defaults to throw BadCredentialsException for authentication failures to prevent user enumeration,
    // so we won't handle UsernameNotFoundException separately unless we want to change this behavior
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpRes<Void>> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Authentication failed for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.UNAUTHORIZED, "Error while trying to login");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpRes<Void>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Unauthorized access to {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.UNAUTHORIZED, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpRes<Void>> handleGenericException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("An error occurred while processing {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
    }
}
