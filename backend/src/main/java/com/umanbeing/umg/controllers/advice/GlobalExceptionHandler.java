package com.umanbeing.umg.controllers.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpRes<Void>> handleGenericException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("An error occurred while processing {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
    }
}
